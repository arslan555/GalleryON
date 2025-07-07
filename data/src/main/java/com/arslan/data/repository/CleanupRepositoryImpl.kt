package com.arslan.data.repository

import com.arslan.data.cleaner.CleanupManager
import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.repository.CleanupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CleanupRepositoryImpl @Inject constructor(
    private val cleanupManager: CleanupManager
) : CleanupRepository {

    override fun findDuplicateImages(): Flow<List<CleanupItem>> {
        return cleanupManager.findDuplicateImages()
    }

    override fun findLargeVideos(thresholdMB: Int): Flow<List<CleanupItem>> {
        return cleanupManager.findLargeVideos(thresholdMB)
    }

    override fun findOldMedia(daysOld: Int): Flow<List<CleanupItem>> {
        return cleanupManager.findOldMedia(daysOld)
    }

    override fun getAllCleanupItems(
        largeVideoThresholdMB: Int,
        oldMediaDays: Int
    ): Flow<List<CleanupItem>> {
        return cleanupManager.getAllCleanupItems(largeVideoThresholdMB, oldMediaDays)
    }

    override suspend fun deleteMediaItems(mediaItems: List<Long>): Result<Unit> {
        return cleanupManager.deleteMediaItems(mediaItems)
    }
} 