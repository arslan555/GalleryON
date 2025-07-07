package com.arslan.domain.usecase.cleaner

import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.repository.CleanupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindOldMediaUseCase @Inject constructor(
    private val cleanupRepository: CleanupRepository
) {
    operator fun invoke(daysOld: Int): Flow<List<CleanupItem>> {
        return cleanupRepository.findOldMedia(daysOld)
    }
} 