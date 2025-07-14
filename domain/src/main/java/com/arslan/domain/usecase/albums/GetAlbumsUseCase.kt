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
                val groupedAlbums = mediaItems
                    .groupBy { it.folderName ?: "Unknown" }
                    .map { (folderName, items) ->
                        val cleanFolderName = folderName.trimEnd('/')
                        val albumName = cleanFolderName.substringAfterLast("/").ifBlank { "Unknown" }
                        AlbumItem(
                            id = folderName,
                            name = albumName,
                            mediaItems = items.sortedByDescending { it.dateTaken }
                        )
                    }
                    .sortedByDescending { it.mediaItems.maxOfOrNull { it.dateTaken ?: 0L } }

                val allPhotosAlbum = AlbumItem(
                    id = "ALL_PHOTOS",
                    name = "All Photos",
                    mediaItems = mediaItems.sortedByDescending { it.dateTaken }
                )

                listOf(allPhotosAlbum) + groupedAlbums
            }
    }
}