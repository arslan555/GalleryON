package com.arslan.feature.smartcleaner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arslan.domain.usecase.cleaner.DeleteMediaItemsUseCase
import com.arslan.domain.usecase.cleaner.FindDuplicateImagesUseCase
import com.arslan.domain.usecase.cleaner.FindLargeVideosUseCase
import com.arslan.domain.usecase.cleaner.FindOldMediaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartCleanerViewModel @Inject constructor(
    private val findDuplicateImagesUseCase: FindDuplicateImagesUseCase,
    private val findLargeVideosUseCase: FindLargeVideosUseCase,
    private val findOldMediaUseCase: FindOldMediaUseCase,
    private val deleteMediaItemsUseCase: DeleteMediaItemsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SmartCleanerState>(SmartCleanerState.Loading)
    val state: StateFlow<SmartCleanerState> = _state.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    
    // Track current active filter
    private val _currentFilter = MutableStateFlow<CleanupFilter>(CleanupFilter.All)
    val currentFilter: StateFlow<CleanupFilter> = _currentFilter.asStateFlow()

    init {
        loadAllCleanupItems()
    }

    fun onEvent(event: SmartCleanerEvent) {
        when (event) {
            SmartCleanerEvent.Refresh -> {
                loadAllCleanupItems()
            }

            is SmartCleanerEvent.ItemSelected -> {
                val currentSelected = _selectedItems.value.toMutableSet()
                if (event.selected) {
                    currentSelected.add(event.itemId)
                } else {
                    currentSelected.remove(event.itemId)
                }
                _selectedItems.value = currentSelected
                updateStateWithSelection()
            }

            SmartCleanerEvent.SelectAll -> {
                val currentState = _state.value
                if (currentState is SmartCleanerState.Success) {
                    _selectedItems.value = currentState.cleanupItems.map { it.id }.toSet()
                    updateStateWithSelection()
                }
            }
            
            SmartCleanerEvent.DeselectAll -> {
                _selectedItems.value = emptySet()
                updateStateWithSelection()
            }

            SmartCleanerEvent.DeleteSelected -> {
                deleteSelectedItems()
            }

            SmartCleanerEvent.ScanForDuplicates -> {
                _currentFilter.value = CleanupFilter.Duplicates
                scanForDuplicates()
            }

            SmartCleanerEvent.ScanForLargeVideos -> {
                _currentFilter.value = CleanupFilter.LargeVideos
                scanForLargeVideos()
            }

            SmartCleanerEvent.ScanForOldMedia -> {
                _currentFilter.value = CleanupFilter.OldMedia
                scanForOldMedia()
            }
        }
    }

    private fun loadAllCleanupItems() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentFilter.value = CleanupFilter.All
            combine(
                findDuplicateImagesUseCase(),
                findLargeVideosUseCase(100), // 100MB threshold
                findOldMediaUseCase(365) // 1 year old
            ) { duplicates, largeVideos, oldMedia ->
                duplicates + largeVideos + oldMedia
            }
            .onStart {
                _state.value = SmartCleanerState.Loading
            }
            .catch { exception ->
                _state.value = SmartCleanerState.Error(exception.message ?: "Unknown error")
            }
            .collect { cleanupItems ->
                val totalSpaceSaved = cleanupItems.sumOf { it.totalSize }
                _state.value = SmartCleanerState.Success(
                    cleanupItems = cleanupItems,
                    totalSpaceSaved = totalSpaceSaved,
                    selectedItems = _selectedItems.value
                )
            }
        }
    }

    private fun updateStateWithSelection() {
        val currentState = _state.value
        if (currentState is SmartCleanerState.Success) {
            _state.value = currentState.copy(selectedItems = _selectedItems.value)
        }
    }

    private fun deleteSelectedItems() {
        val currentState = _state.value
        if (currentState !is SmartCleanerState.Success) return

        val selectedCleanupItems = currentState.cleanupItems.filter { it.id in _selectedItems.value }
        val allMediaIds = selectedCleanupItems.flatMap { it.mediaItems.map { media -> media.id } }

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = SmartCleanerState.Deleting

            deleteMediaItemsUseCase(allMediaIds)
                .onSuccess {
                    _state.value = SmartCleanerState.DeletionComplete(allMediaIds.size)
                    _selectedItems.value = emptySet()
                    // Reload the list after deletion
                    loadAllCleanupItems()
                }
                .onFailure { exception ->
                    _state.value = SmartCleanerState.Error(exception.message ?: "Failed to delete items")
                }
        }
    }

    private fun scanForDuplicates() {
        viewModelScope.launch(Dispatchers.IO) {
            findDuplicateImagesUseCase()
                .onStart {
                    _state.value = SmartCleanerState.Loading
                }
                .catch { exception ->
                    _state.value = SmartCleanerState.Error(exception.message ?: "Failed to scan for duplicates")
                }
                .collect { duplicates ->
                    val totalSpaceSaved = duplicates.sumOf { it.totalSize }
                    _state.value = SmartCleanerState.Success(
                        cleanupItems = duplicates,
                        totalSpaceSaved = totalSpaceSaved,
                        selectedItems = _selectedItems.value
                    )
                }
        }
    }

    private fun scanForLargeVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            findLargeVideosUseCase(100) // 100MB threshold
                .onStart {
                    _state.value = SmartCleanerState.Loading
                }
                .catch { exception ->
                    _state.value = SmartCleanerState.Error(exception.message ?: "Failed to scan for large videos")
                }
                .collect { largeVideos ->
                    val totalSpaceSaved = largeVideos.sumOf { it.totalSize }
                    _state.value = SmartCleanerState.Success(
                        cleanupItems = largeVideos,
                        totalSpaceSaved = totalSpaceSaved,
                        selectedItems = _selectedItems.value
                    )
                }
        }
    }

    private fun scanForOldMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            findOldMediaUseCase(365) // 1 year old
                .onStart {
                    _state.value = SmartCleanerState.Loading
                }
                .catch { exception ->
                    _state.value = SmartCleanerState.Error(exception.message ?: "Failed to scan for old media")
                }
                .collect { oldMedia ->
                    val totalSpaceSaved = oldMedia.sumOf { it.totalSize }
                    _state.value = SmartCleanerState.Success(
                        cleanupItems = oldMedia,
                        totalSpaceSaved = totalSpaceSaved,
                        selectedItems = _selectedItems.value
                    )
                }
        }
    }
} 