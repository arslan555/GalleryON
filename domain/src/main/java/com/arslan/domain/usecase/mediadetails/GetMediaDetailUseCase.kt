package com.arslan.domain.usecase.mediadetails

import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMediaDetailUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    fun execute(mediaId: Long): Flow<MediaItem?> {
        return mediaRepository.getAllMediaItems()
            .map { mediaItems ->
                mediaItems.find { it.id == mediaId }
            }
    }
}