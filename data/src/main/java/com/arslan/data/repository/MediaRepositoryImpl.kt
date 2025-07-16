package com.arslan.data.repository

import com.arslan.data.media.MediaManager
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaManager: MediaManager
) : MediaRepository {

    override fun getAllMediaItems(): Flow<List<MediaItem>> = flow {
        mediaManager.loadAllMedia()
        emit(mediaManager.allMediaItems.value)
    }

    override suspend fun createAlbum(folderName: String): Boolean = mediaManager.createAlbum(folderName)
}