package com.arslan.domain.usecase.media

import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMediaItemsUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    fun execute(albumId: String?): Flow<List<MediaItem>> {
        return mediaRepository.getAllMediaItems()
            .map { mediaItems ->
                mediaItems
                    .filter { it.folderName?.startsWith(albumId ?: "") == true }
                    .sortedByDescending { it.dateTaken }
            }
    }
}