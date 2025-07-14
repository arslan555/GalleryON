package com.arslan.data.cleaner

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaDeletionHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityProvider: ActivityProvider
) {
    private val contentResolver: ContentResolver = context.contentResolver
    private val tag = "MediaDeletionHelper"

    companion object {
        const val DELETE_REQUEST_CODE = 1001
        // Global callback instance (in a real app, you might want to use a more sophisticated approach)
        private var deletionCallback: DeletionCallback? = null
        
        fun setDeletionCallback(callback: DeletionCallback?) {
            deletionCallback = callback
        }
    }

    /**
     * Delete media items using the appropriate method based on Android version
     */
    suspend fun deleteMediaItems(mediaItemIds: List<Long>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Attempting to delete ${mediaItemIds.size} media items")
            
            val uris = getUrisFromMediaIds(mediaItemIds)
            if (uris.isEmpty()) {
                Log.w(tag, "No valid URIs found for deletion")
                return@withContext Result.failure(Exception("No valid URIs found for deletion"))
            }

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    // Android 11+ (API 30+): Use MediaStore.createDeleteRequest()
                    Log.d(tag, "Using MediaStore.createDeleteRequest() for Android 11+")
                    useMediaStoreDeleteRequest(uris)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    // Android 10 (API 29): Use MediaStore deletion
                    Log.d(tag, "Using MediaStore deletion for Android 10")
                    deleteWithMediaStore(uris)
                }
                else -> {
                    // Android 9 and below: Use legacy approach
                    Log.d(tag, "Using legacy deletion for Android 9 and below")
                    deleteLegacy(uris)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting media items: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Use MediaStore.createDeleteRequest() for Android 11+ (API 30+)
     */
    private suspend fun useMediaStoreDeleteRequest(uris: List<Uri>): Result<Unit> = withContext(Dispatchers.Main) {
        return@withContext try {
            Log.d(tag, "Creating delete request for ${uris.size} URIs")
            
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.createDeleteRequest(contentResolver, uris)
            } else {
                // For Android 10 (API 29), perform direct MediaStore deletion and return success/failure
                var deletedCount = 0
                uris.forEach { uri ->
                    try {
                        val deleted = contentResolver.delete(uri, null, null)
                        if (deleted > 0) {
                            deletedCount++
                            Log.d(tag, "Successfully deleted: $uri")
                        } else {
                            Log.w(tag, "Failed to delete: $uri")
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Error deleting $uri: ${e.message}", e)
                    }
                }
                Log.d(tag, "Deleted $deletedCount out of ${uris.size} files (API 29)")
                // Return early with a Result
                if (deletedCount > 0) {
                    return@withContext Result.success(Unit)
                } else {
                    return@withContext Result.failure(Exception("No files were deleted (API 29)"))
                }
            }

            // Get the current activity from the provider
            val activity = activityProvider.getCurrentActivity()
            if (activity != null) {
                // Use the modern activity result launcher approach
                try {
                    // For now, we'll use the deprecated method but with proper error handling
                    // In a production app, you might want to implement a custom solution
                    activity.startIntentSenderForResult(
                        pendingIntent.intentSender,
                        DELETE_REQUEST_CODE,
                        null,
                        0,
                        0,
                        0
                    )
                    Log.d(tag, "Started delete request intent sender")
                    Result.success(Unit)
                } catch (e: Exception) {
                    Log.e(tag, "Error starting intent sender: ${e.message}", e)
                    // Fallback to direct deletion if intent sender fails
                    deleteWithMediaStore(uris)
                }
            } else {
                Log.e(tag, "No activity available, cannot start intent sender")
                Result.failure(Exception("No activity available for MediaStore.createDeleteRequest()"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating delete request: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Delete using MediaStore for Android 10 (API 29)
     */
    private suspend fun deleteWithMediaStore(uris: List<Uri>): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            var deletedCount = 0
            
            uris.forEach { uri ->
                try {
                    val deleted = contentResolver.delete(uri, null, null)
                    if (deleted > 0) {
                        deletedCount++
                        Log.d(tag, "Successfully deleted: $uri")
                    } else {
                        Log.w(tag, "Failed to delete: $uri")
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error deleting $uri: ${e.message}", e)
                }
            }
            
            Log.d(tag, "Deleted $deletedCount out of ${uris.size} files")
            
            if (deletedCount > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No files were deleted"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in MediaStore deletion: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Legacy deletion for Android 9 and below
     */
    private suspend fun deleteLegacy(uris: List<Uri>): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            var deletedCount = 0
            
            uris.forEach { uri ->
                try {
                    // Try to get the file path
                    val filePath = getFilePathFromUri(uri)
                    
                    if (filePath != null) {
                        val file = File(filePath)
                        if (file.exists() && file.delete()) {
                            deletedCount++
                            Log.d(tag, "Successfully deleted file: $filePath")
                        } else {
                            Log.w(tag, "Failed to delete file: $filePath")
                        }
                    } else {
                        // Fallback to MediaStore deletion
                        val deleted = contentResolver.delete(uri, null, null)
                        if (deleted > 0) {
                            deletedCount++
                            Log.d(tag, "Successfully deleted via MediaStore: $uri")
                        } else {
                            Log.w(tag, "Failed to delete via MediaStore: $uri")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error deleting $uri: ${e.message}", e)
                }
            }
            
            Log.d(tag, "Deleted $deletedCount out of ${uris.size} files")
            
            if (deletedCount > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No files were deleted"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in legacy deletion: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Handle the result of MediaStore.createDeleteRequest()
     */
    fun handleDeleteResult(requestCode: Int, resultCode: Int): Boolean {
        if (requestCode == DELETE_REQUEST_CODE) {
            return when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d(tag, "User confirmed deletion")
                    deletionCallback?.onDeletionResult(true, "Files deleted successfully")
                    true
                }
                Activity.RESULT_CANCELED -> {
                    Log.d(tag, "User canceled deletion")
                    deletionCallback?.onDeletionResult(false, "Deletion was cancelled")
                    false
                }
                else -> {
                    Log.w(tag, "Unknown result code: $resultCode")
                    deletionCallback?.onDeletionResult(false, "Unknown result code: $resultCode")
                    false
                }
            }
        }
        return false
    }

    /**
     * Get URIs from media IDs for use in deletion
     */
    private fun getUrisFromMediaIds(mediaItemIds: List<Long>): List<Uri> {
        val uris = mutableListOf<Uri>()
        
        mediaItemIds.forEach { id ->
            try {
                // Try to get the file information first
                val filesUri = Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), id.toString())
                
                val cursor = contentResolver.query(
                    filesUri,
                    arrayOf(
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.MEDIA_TYPE,
                        MediaStore.Files.FileColumns.DISPLAY_NAME
                    ),
                    null,
                    null,
                    null
                )

                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val filePath = c.getString(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                        val mediaType = c.getInt(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))
                        val displayName = c.getString(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                        
                        Log.d(tag, "Getting URI for: $displayName ($filePath), Media type: $mediaType")
                        
                        // Create the appropriate content URI based on media type
                        val contentUri = when (mediaType) {
                            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                                Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                            }
                            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                                Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id.toString())
                            }
                            else -> filesUri
                        }
                        
                        uris.add(contentUri)
                        Log.d(tag, "Added URI: $contentUri")
                    } else {
                        Log.w(tag, "No data found for media ID: $id")
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error getting URI for media ID $id: ${e.message}", e)
            }
        }
        
        Log.d(tag, "Generated ${uris.size} URIs from ${mediaItemIds.size} media IDs")
        return uris
    }

    /**
     * Get file path from URI (for legacy Android versions)
     */
    private fun getFilePathFromUri(uri: Uri): String? {
        return try {
            val cursor = contentResolver.query(uri, arrayOf(MediaStore.Files.FileColumns.DATA), null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    c.getString(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting file path from URI $uri: ${e.message}", e)
            null
        }
    }

    // Callback interface for deletion results
    interface DeletionCallback {
        fun onDeletionResult(success: Boolean, message: String)
    }
} 