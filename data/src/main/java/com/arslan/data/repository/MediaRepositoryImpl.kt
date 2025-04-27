package com.arslan.data.repository


import com.arslan.data.media.MediaManager
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaManager: MediaManager
) : MediaRepository {

    override  fun getAllMediaItems(): Flow<List<MediaItem>> {
        mediaManager.loadAllMedia()
        return mediaManager.allMediaItems
    }
}