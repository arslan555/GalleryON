package com.arslan.domain.usecase.cleaner

import com.arslan.domain.repository.CleanupRepository
import javax.inject.Inject

class DeleteMediaItemsUseCase @Inject constructor(
    private val cleanupRepository: CleanupRepository
) {
    suspend operator fun invoke(mediaItemIds: List<Long>): Result<Unit> {
        return cleanupRepository.deleteMediaItems(mediaItemIds)
    }
} 