package com.arslan.feature.smartcleaner.presentation

sealed interface SmartCleanerEvent {
    data object Refresh : SmartCleanerEvent
    data class ItemSelected(val itemId: String, val selected: Boolean) : SmartCleanerEvent
    data object SelectAll : SmartCleanerEvent
    data object DeselectAll : SmartCleanerEvent
    data object DeleteSelected : SmartCleanerEvent
    data object ScanForDuplicates : SmartCleanerEvent
    data object ScanForLargeVideos : SmartCleanerEvent
    data object ScanForOldMedia : SmartCleanerEvent
} 