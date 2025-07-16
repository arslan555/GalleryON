package com.arslan.domain.repository

import com.arslan.domain.model.media.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
   /*
    **
    * Returns a list of media items (images and videos).
    */
    fun getAllMediaItems(): Flow<List<MediaItem>>

    /**
     * Create a new album (folder) with the given name. Returns true if successful.
     */
    suspend fun createAlbum(folderName: String): Boolean
}