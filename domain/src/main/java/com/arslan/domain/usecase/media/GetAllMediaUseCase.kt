package com.arslan.domain.usecase.media

import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow


class GetAllMediaUseCase(
    private val mediaRepository: MediaRepository
) {
    fun execute(): Flow<List<MediaItem>> {
        return mediaRepository.getAllMedia()
    }
}