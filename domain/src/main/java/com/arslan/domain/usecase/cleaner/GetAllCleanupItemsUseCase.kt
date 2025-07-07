package com.arslan.domain.usecase.cleaner

import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.repository.CleanupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCleanupItemsUseCase @Inject constructor(
    private val cleanupRepository: CleanupRepository
) {
    operator fun invoke(
        largeVideoThresholdMB: Int = 100,
        oldMediaDays: Int = 365
    ): Flow<List<CleanupItem>> {
        return cleanupRepository.getAllCleanupItems(largeVideoThresholdMB, oldMediaDays)
    }
} 