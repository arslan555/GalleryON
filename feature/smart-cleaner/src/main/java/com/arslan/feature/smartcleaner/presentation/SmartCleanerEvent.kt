package com.arslan.feature.smartcleaner.presentation

sealed interface SmartCleanerEvent {
    data object Refresh : SmartCleanerEvent
    data object ScanForDuplicates : SmartCleanerEvent
    data object ScanForLargeVideos : SmartCleanerEvent
    data object ScanForOldMedia : SmartCleanerEvent
} 