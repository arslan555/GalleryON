package com.arslan.data.media

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaManager @Inject constructor(
    private val context: Context
) {

    private val _allMediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val allMediaItems = _allMediaItems.asStateFlow()

    suspend fun loadAllMedia() = withContext(Dispatchers.IO) {
        // Always reload to ensure we have the latest data
        android.util.Log.d("MediaManager", "Loading all media from OS...")

        val mediaItems = mutableListOf<MediaItem>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.RELATIVE_PATH,
            MediaStore.Video.VideoColumns.DURATION
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateTaken = cursor.getLongOrNull(dateTakenColumn)
                val size = cursor.getLongOrNull(sizeColumn)
                val mediaTypeValue = cursor.getInt(mediaTypeColumn)
                val relativePath = cursor.getString(relativePathColumn)
                val duration = cursor.getLongOrNull(durationColumn)

                val mediaType = when (mediaTypeValue) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaType.Image
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaType.Video
                    else -> continue
                }

                val contentUri = MediaStore.Files.getContentUri("external", id)

                mediaItems.add(
                    MediaItem(
                        id = id,
                        name = name,
                        uri = contentUri.toString(),
                        dateTaken = dateTaken,
                        mediaType = mediaType,
                        folderName = relativePath,
                        size = size,
                        duration = if (mediaType == MediaType.Video) duration else null
                    )
                )
            }
        }

        _allMediaItems.value = mediaItems
    }

    /**
     * Get all images from the loaded media
     */
    fun getImages(): List<MediaItem> {
        return allMediaItems.value.filter { it.mediaType == MediaType.Image }
    }

    /**
     * Get all videos from the loaded media
     */
    private fun getVideos(): List<MediaItem> {
        return allMediaItems.value.filter { it.mediaType == MediaType.Video }
    }

    /**
     * Get media items older than specified timestamp
     */
    fun getOldMedia(cutoffTime: Long): List<MediaItem> {
        return allMediaItems.value.filter { (it.dateTaken ?: 0) < cutoffTime }
    }

    /**
     * Get large videos above specified size threshold
     */
    fun getLargeVideos(thresholdBytes: Long): List<MediaItem> {
        return getVideos().filter { (it.size ?: 0) > thresholdBytes }
    }

    /**
     * Clear cached media items (useful after deletions)
     */
    fun clearCache() {
        _allMediaItems.value = emptyList()
    }

    /**
     * Remove specific media items from cache (for immediate UI update after deletion)
     */
    fun removeFromCache(mediaIds: List<Long>) {
        val currentItems = _allMediaItems.value.toMutableList()
        currentItems.removeAll { it.id in mediaIds }
        _allMediaItems.value = currentItems
        android.util.Log.d("MediaManager", "Removed ${mediaIds.size} items from cache. Remaining: ${_allMediaItems.value.size}")
    }

    /**
     * Force reload all media from OS (for background sync)
     */
    private suspend fun forceReload() = withContext(Dispatchers.IO) {
        _allMediaItems.value = emptyList() // Clear first
        loadAllMedia() // Reload from OS
    }

    /**
     * Smart refresh: immediate cache update + background OS sync
     */
    suspend fun smartRefresh(deletedMediaIds: List<Long>) = withContext(Dispatchers.IO) {
        android.util.Log.d("MediaManager", "Starting smart refresh for ${deletedMediaIds.size} items")
        
        // Step 1: Immediate cache update for fast UI response
        removeFromCache(deletedMediaIds)
        
        // Step 2: Force reload to ensure cache is updated with latest data
        // This is important for duplicate detection to work correctly
        try {
            forceReload()
            android.util.Log.d("MediaManager", "Smart refresh completed successfully")
        } catch (e: Exception) {
            android.util.Log.e("MediaManager", "Smart refresh failed: ${e.message}", e)
            throw e
        }
    }

    private fun Cursor.getLongOrNull(columnIndex: Int): Long? {
        return if (isNull(columnIndex)) null else getLong(columnIndex)
    }
}
