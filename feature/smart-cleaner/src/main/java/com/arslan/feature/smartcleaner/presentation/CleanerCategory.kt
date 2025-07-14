package com.arslan.feature.smartcleaner.presentation

// Sealed class for cleaner categories
sealed interface CleanerCategory {
    data object Duplicates : CleanerCategory
    data object LargeVideos : CleanerCategory
    data object OldMedia : CleanerCategory
} 