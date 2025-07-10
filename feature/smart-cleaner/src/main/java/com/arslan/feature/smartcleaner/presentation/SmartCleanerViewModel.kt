package com.arslan.feature.smartcleaner.presentation

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.usecase.cleaner.DeleteMediaItemsUseCase
import com.arslan.domain.usecase.cleaner.FindDuplicateImagesUseCase
import com.arslan.data.cleaner.MediaDeletionHelper
import com.arslan.domain.model.cleaner.CleanupType
import com.arslan.domain.usecase.cleaner.FindLargeVideosUseCase
import com.arslan.domain.usecase.cleaner.FindOldMediaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class SmartCleanerViewModel @Inject constructor(
    private val findDuplicateImagesUseCase: FindDuplicateImagesUseCase,
    private val findLargeVideosUseCase: FindLargeVideosUseCase,
    private val findOldMediaUseCase: FindOldMediaUseCase,
    private val deleteMediaItemsUseCase: DeleteMediaItemsUseCase
) : ViewModel(), MediaDeletionHelper.DeletionCallback {

    private val _screenState = MutableStateFlow(SmartCleanerScreenState())
    val screenState: StateFlow<SmartCleanerScreenState> = _screenState.asStateFlow()

    // Generalized selection state management
    private val _selectedItems = MutableStateFlow<Map<CleanerCategory, Set<String>>>(emptyMap())
    val selectedItems: StateFlow<Map<CleanerCategory, Set<String>>> = _selectedItems.asStateFlow()



    // Track the last deleted items for callback handling
    private var lastDeletedItems: List<Long> = emptyList()

    init {
        loadAllCategories()
        // Set up the deletion callback
        MediaDeletionHelper.setDeletionCallback(this)
    }

    private fun loadAllCategories() {
        // Duplicates (grouped as before)
        viewModelScope.launch {
            _screenState.update { it.copy(duplicates = CleanerCategoryState(isLoading = true, error = null)) }
            try {
                val items = findDuplicateImagesUseCase().firstOrNull() ?: emptyList()
                _screenState.update { it.copy(duplicates = CleanerCategoryState(isLoading = false, items = items)) }
            } catch (e: Exception) {
                _screenState.update { it.copy(duplicates = CleanerCategoryState(isLoading = false, error = e.message ?: "Failed to load duplicates")) }
            }
        }
        // Large Media (split into individual CleanupItems)
        viewModelScope.launch {
            _screenState.update { it.copy(largeMedia = CleanerCategoryState(isLoading = true, error = null)) }
            try {
                val items = findLargeVideosUseCase(100).firstOrNull() ?: emptyList()
                val splitItems = items.flatMap { group ->
                    group.mediaItems.map { media ->
                        CleanupItem(
                            id = media.id.toString(),
                            mediaItems = listOf(media),
                            cleanupType = CleanupType.LargeVideos(100),
                            totalSize = media.size ?: 0L,
                            itemCount = 1
                        )
                    }
                }
                _screenState.update { it.copy(largeMedia = CleanerCategoryState(isLoading = false, items = splitItems)) }
            } catch (e: Exception) {
                _screenState.update { it.copy(largeMedia = CleanerCategoryState(isLoading = false, error = e.message ?: "Failed to load large videos")) }
            }
        }
        // Old Media (split into individual CleanupItems)
        viewModelScope.launch {
            _screenState.update { it.copy(oldMedia = CleanerCategoryState(isLoading = true, error = null)) }
            try {
                val items = findOldMediaUseCase(365).firstOrNull() ?: emptyList()
                val splitItems = items.flatMap { group ->
                    group.mediaItems.map { media ->
                        CleanupItem(
                            id = media.id.toString(),
                            mediaItems = listOf(media),
                            cleanupType = CleanupType.OldMedia(365),
                            totalSize = media.size ?: 0L,
                            itemCount = 1
                        )
                    }
                }
                _screenState.update { it.copy(oldMedia = CleanerCategoryState(isLoading = false, items = splitItems)) }
            } catch (e: Exception) {
                _screenState.update { it.copy(oldMedia = CleanerCategoryState(isLoading = false, error = e.message ?: "Failed to load old media")) }
            }
        }
    }

    // Generalized selection management
    fun toggleSelection(category: CleanerCategory, itemId: String, selected: Boolean) {
        _selectedItems.update { currentMap ->
            val currentSet = currentMap[category] ?: emptySet()
            val newSet = if (selected) {
                currentSet + itemId
            } else {
                currentSet - itemId
            }
            currentMap + (category to newSet)
        }
    }

    fun selectAll(category: CleanerCategory, items: List<CleanupItem>) {
        _selectedItems.update { currentMap ->
            currentMap + (category to items.map { it.id }.toSet())
        }
    }

    fun clearAll(category: CleanerCategory) {
        _selectedItems.update { currentMap ->
            currentMap + (category to emptySet())
        }
    }

    // Generalized deletion method
    fun deleteSelected(category: CleanerCategory) {
        val selectedIds = _selectedItems.value[category] ?: emptySet()
        val items = when (category) {
            is CleanerCategory.Duplicates -> {
                val duplicateGroups = _screenState.value.duplicates.items
                    .filter { selectedIds.contains(it.id) }
                duplicateGroups.flatMap { it.mediaItems.drop(1).map { media -> media.id } }
            }
            is CleanerCategory.LargeVideos -> {
                _screenState.value.largeMedia.items
                    .filter { selectedIds.contains(it.id) }
                    .flatMap { it.mediaItems }
                    .map { it.id }
            }
            is CleanerCategory.OldMedia -> {
                _screenState.value.oldMedia.items
                    .filter { selectedIds.contains(it.id) }
                    .flatMap { it.mediaItems }
                    .map { it.id }
            }
        }

        if (items.isEmpty()) return

        // Set isDeleting
        setCategoryDeleting(category, true)
        viewModelScope.launch {
            lastDeletedItems = items
            deleteMediaItemsUseCase(items)
                .onSuccess {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        clearAll(category)
                    } else {
                        updateUIAfterDeletion(items)
                        clearAll(category)
                    }
                }
                .onFailure { /* handle error */ }
            // Reset isDeleting
            setCategoryDeleting(category, false)
        }
    }



    // --- Event handler for category reloads ---
    fun onEvent(event: SmartCleanerEvent) {
        when (event) {
            SmartCleanerEvent.Refresh -> loadAllCategories()
            SmartCleanerEvent.ScanForDuplicates -> {
                viewModelScope.launch {
                    _screenState.update { it.copy(duplicates = CleanerCategoryState(isLoading = true, error = null)) }
                    try {
                        val items = findDuplicateImagesUseCase().firstOrNull() ?: emptyList()
                        _screenState.update { it.copy(duplicates = CleanerCategoryState(isLoading = false, items = items)) }
                    } catch (e: Exception) {
                        _screenState.update { it.copy(duplicates = CleanerCategoryState(isLoading = false, error = e.message ?: "Failed to load duplicates")) }
                    }
                }
            }
            SmartCleanerEvent.ScanForLargeVideos -> {
                viewModelScope.launch {
                    _screenState.update { it.copy(largeMedia = CleanerCategoryState(isLoading = true, error = null)) }
                    try {
                        val items = findLargeVideosUseCase(100).firstOrNull() ?: emptyList()
                        _screenState.update { it.copy(largeMedia = CleanerCategoryState(isLoading = false, items = items)) }
                    } catch (e: Exception) {
                        _screenState.update { it.copy(largeMedia = CleanerCategoryState(isLoading = false, error = e.message ?: "Failed to load large videos")) }
                    }
                }
            }
            SmartCleanerEvent.ScanForOldMedia -> {
                viewModelScope.launch {
                    _screenState.update { it.copy(oldMedia = CleanerCategoryState(isLoading = true, error = null)) }
                    try {
                        val items = findOldMediaUseCase(365).firstOrNull() ?: emptyList()
                        _screenState.update { it.copy(oldMedia = CleanerCategoryState(isLoading = false, items = items)) }
                    } catch (e: Exception) {
                        _screenState.update { it.copy(oldMedia = CleanerCategoryState(isLoading = false, error = e.message ?: "Failed to load old media")) }
                    }
                }
            }
        }
    }

    // --- Callback ---
    override fun onDeletionResult(success: Boolean, message: String) {
        if (success) {
            updateUIAfterDeletion(lastDeletedItems)
        }
    }

    // --- UI Update ---
    private fun updateUIAfterDeletion(deletedItemIds: List<Long>) {
        viewModelScope.launch {
            // Helper function to update cleanup items
            fun updateCleanupItems(items: List<CleanupItem>, minRemainingItems: Int = 1): List<CleanupItem> {
                return items.mapNotNull { cleanupItem ->
                    val remainingItems = cleanupItem.mediaItems.filter { it.id !in deletedItemIds }
                    if (remainingItems.size >= minRemainingItems) {
                        cleanupItem.copy(
                            mediaItems = remainingItems,
                            itemCount = remainingItems.size,
                            totalSize = remainingItems.sumOf { it.size ?: 0 }
                        )
                    } else null
                }
            }

            // Update all categories in a single state update
            _screenState.update { currentState ->
                currentState.copy(
                    duplicates = currentState.duplicates.copy(
                        items = updateCleanupItems(currentState.duplicates.items, minRemainingItems = 2)
                    ),
                    largeMedia = currentState.largeMedia.copy(
                        items = updateCleanupItems(currentState.largeMedia.items)
                    ),
                    oldMedia = currentState.oldMedia.copy(
                        items = updateCleanupItems(currentState.oldMedia.items)
                    )
                )
            }
        }
    }

    private fun setCategoryDeleting(category: CleanerCategory, isDeleting: Boolean) {
        _screenState.update { state ->
            when (category) {
                is CleanerCategory.Duplicates -> state.copy(duplicates = state.duplicates.copy(isDeleting = isDeleting))
                is CleanerCategory.LargeVideos -> state.copy(largeMedia = state.largeMedia.copy(isDeleting = isDeleting))
                is CleanerCategory.OldMedia -> state.copy(oldMedia = state.oldMedia.copy(isDeleting = isDeleting))
            }
        }
    }
} 