package com.arslan.feature.smartcleaner.presentation

import com.arslan.domain.model.cleaner.CleanupItem

data class CleanerCategoryState(
    val isLoading: Boolean = false,
    val items: List<CleanupItem> = emptyList(),
    val error: String? = null
)

data class SmartCleanerScreenState(
    val duplicates: CleanerCategoryState = CleanerCategoryState(isLoading = true),
    val largeMedia: CleanerCategoryState = CleanerCategoryState(isLoading = true),
    val oldMedia: CleanerCategoryState = CleanerCategoryState(isLoading = true)
) 