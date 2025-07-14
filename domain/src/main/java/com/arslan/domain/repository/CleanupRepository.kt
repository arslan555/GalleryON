package com.arslan.domain.repository

import com.arslan.domain.model.cleaner.CleanupItem
import kotlinx.coroutines.flow.Flow

interface CleanupRepository {
    fun findDuplicateImages(): Flow<List<CleanupItem>>
    fun findLargeVideos(thresholdMB: Int): Flow<List<CleanupItem>>
    fun findOldMedia(daysOld: Int): Flow<List<CleanupItem>>
    fun getAllCleanupItems(
        largeVideoThresholdMB: Int = 100,
        oldMediaDays: Int = 365
    ): Flow<List<CleanupItem>>
    suspend fun deleteMediaItems(mediaItems: List<Long>): Result<Unit>
} 