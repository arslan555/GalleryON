package com.arslan.domain.usecase.albums

import com.arslan.domain.model.album.AlbumItem
import com.arslan.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    fun execute(): Flow<List<AlbumItem>> {
        return mediaRepository.getAllMediaItems()
            .map { mediaItems ->
                mediaItems
                    .groupBy { it.folderName ?: "Unknown" }
                    .map { (folderName, items) ->
                        AlbumItem(
                            id = folderName,
                            name = folderName.substringAfterLast("/").removeSuffix("/"),
                            mediaItems = items.sortedByDescending { it.dateTaken }
                        )
                    }
                    .sortedByDescending { it.mediaItems.maxOfOrNull { it.dateTaken ?: 0L } }
            }
    }
}