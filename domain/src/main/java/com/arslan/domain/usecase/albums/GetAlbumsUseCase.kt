package com.arslan.domain.usecase.albums

import com.arslan.domain.model.album.AlbumItem
import com.arslan.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class GetAlbumsUseCase(
    private val mediaRepository: MediaRepository
) {
    fun execute(): Flow<List<AlbumItem>> {
        return mediaRepository.getAlbums()
    }
}