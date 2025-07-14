package com.arslan.data.cleaner

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.arslan.data.media.MediaManager
import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.model.cleaner.CleanupType
import com.arslan.domain.model.media.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CleanupManager @Inject constructor(
    context: Context,
    private val mediaManager: MediaManager,
    private val mediaDeletionHelper: MediaDeletionHelper
) {
    private val contentResolver: ContentResolver = context.contentResolver

    fun findDuplicateImages(): Flow<List<CleanupItem>> = flow {
        android.util.Log.d("CleanupManager", "Finding duplicate images...")
        val images = mediaManager.getImages()
        android.util.Log.d("CleanupManager", "Found ${images.size} images to check for duplicates")
        val duplicates = findDuplicatesByHash(images)
        android.util.Log.d("CleanupManager", "Found ${duplicates.size} duplicate groups")
        emit(duplicates)
    }.flowOn(Dispatchers.IO)

    fun findLargeVideos(thresholdMB: Int): Flow<List<CleanupItem>> = flow {
        val largeVideos = mediaManager.getLargeVideos(thresholdMB * 1024 * 1024L)
        
        if (largeVideos.isNotEmpty()) {
            val totalSize = largeVideos.sumOf { it.size ?: 0 }
            val cleanupItem = CleanupItem(
                id = "large_videos_${thresholdMB}mb",
                mediaItems = largeVideos,
                cleanupType = CleanupType.LargeVideos(thresholdMB),
                totalSize = totalSize,
                itemCount = largeVideos.size
            )
            emit(listOf(cleanupItem))
        } else {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    fun findOldMedia(daysOld: Int): Flow<List<CleanupItem>> = flow {
        val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysOld.toLong())
        val oldMedia = mediaManager.getOldMedia(cutoffTime)
        
        if (oldMedia.isNotEmpty()) {
            val totalSize = oldMedia.sumOf { it.size ?: 0 }
            val cleanupItem = CleanupItem(
                id = "old_media_${daysOld}days",
                mediaItems = oldMedia,
                cleanupType = CleanupType.OldMedia(daysOld),
                totalSize = totalSize,
                itemCount = oldMedia.size
            )
            emit(listOf(cleanupItem))
        } else {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    fun getAllCleanupItems(
        largeVideoThresholdMB: Int = 100,
        oldMediaDays: Int = 365
    ): Flow<List<CleanupItem>> = flow {
        // Ensure media is loaded first
        if (mediaManager.allMediaItems.value.isEmpty()) {
            mediaManager.loadAllMedia()
        }
        
        // Use combine to efficiently merge all cleanup flows
        combine(
            findDuplicateImages(),
            findLargeVideos(largeVideoThresholdMB),
            findOldMedia(oldMediaDays)
        ) { duplicates, largeVideos, oldMedia ->
            duplicates + largeVideos + oldMedia
        }.collect { allCleanupItems ->
            emit(allCleanupItems)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deleteMediaItems(mediaItemIds: List<Long>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Use the MediaDeletionHelper for proper deletion across Android versions
            val result = mediaDeletionHelper.deleteMediaItems(mediaItemIds)
            
            // Remove deleted items from cache on successful deletion
            if (result.isSuccess) {
                mediaManager.removeFromCache(mediaItemIds)
            }
            
            result
        } catch (e: Exception) {
            println("CleanupManager: Exception during deletion: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }



    private suspend fun findDuplicatesByHash(images: List<MediaItem>): List<CleanupItem> = withContext(Dispatchers.IO) {
        val hashGroups = mutableMapOf<String, MutableList<MediaItem>>()
        
        images.forEach { image ->
            val hash = calculateImageHash(image.uri)
            hashGroups.getOrPut(hash) { mutableListOf() }.add(image)
        }

        hashGroups.values
            .filter { it.size > 1 }
            .map { duplicateGroup ->
                val totalSize = duplicateGroup.sumOf { it.size ?: 0 }
                CleanupItem(
                    id = "duplicates_${duplicateGroup.first().id}",
                    mediaItems = duplicateGroup,
                    cleanupType = CleanupType.Duplicates(duplicateGroup.first().uri),
                    totalSize = totalSize,
                    itemCount = duplicateGroup.size
                )
            }
    }

    private fun calculateImageHash(imageUri: String): String {
        return try {
            val uri = Uri.parse(imageUri)
            val inputStream = contentResolver.openInputStream(uri) ?: return imageUri.hashCode().toString()
            
            val bytes = inputStream.readBytes()
            inputStream.close()
            
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(bytes)
            digest.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            imageUri.hashCode().toString()
        }
    }
} 