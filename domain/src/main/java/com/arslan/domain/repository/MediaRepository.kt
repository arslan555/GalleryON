package com.arslan.domain.repository

import com.arslan.domain.model.album.AlbumItem
import com.arslan.domain.model.media.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    /**
     * Returns a list of media items for specific albums (images and videos).
     */
    fun getMediaItems(albumId: String?): Flow<List<MediaItem>>

    /**
     * Returns a list of albums, each containing grouped media items.
     */
    fun getAlbums(): Flow<List<AlbumItem>>
}