package com.arslan.domain.usecase.albums

import com.arslan.domain.repository.MediaRepository
import javax.inject.Inject

class CreateAlbumUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(folderName: String): Boolean = mediaRepository.createAlbum(folderName)
} 