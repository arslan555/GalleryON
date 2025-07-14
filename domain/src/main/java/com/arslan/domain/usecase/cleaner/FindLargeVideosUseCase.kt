package com.arslan.domain.usecase.cleaner

import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.repository.CleanupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindLargeVideosUseCase @Inject constructor(
    private val cleanupRepository: CleanupRepository
) {
    operator fun invoke(thresholdMB: Int): Flow<List<CleanupItem>> {
        return cleanupRepository.findLargeVideos(thresholdMB)
    }
} 