package com.arslan.feature.smartcleaner.presentation

import com.arslan.domain.model.cleaner.CleanupItem

sealed interface SmartCleanerState {
    data object Loading : SmartCleanerState
    data class Success(
        val cleanupItems: List<CleanupItem>,
        val totalSpaceSaved: Long,
        val selectedItems: Set<String> = emptySet()
    ) : SmartCleanerState
    data class Error(val message: String) : SmartCleanerState
    data object Deleting : SmartCleanerState
    data class DeletionComplete(val deletedCount: Int) : SmartCleanerState
}

sealed class CleanupFilter {
    data object All : CleanupFilter()
    data object Duplicates : CleanupFilter()
    data object LargeVideos : CleanupFilter()
    data object OldMedia : CleanupFilter()
} 