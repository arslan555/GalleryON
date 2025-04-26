package com.arslan.domain.usecase.albums

import com.arslan.domain.model.album.AlbumItem
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAlbumsUseCaseTest {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var getAlbumsUseCase: GetAlbumsUseCase

    @Before
    fun setUp() {
        mediaRepository = mockk()
        getAlbumsUseCase = GetAlbumsUseCase(mediaRepository)
    }

    @Test
    fun `invoke should return list of albums`() = runTest {
        // Given
        val mediaItems = listOf(
            MediaItem(
                id = 1L,
                name = "image1.jpg",
                uri = "content://media/external/images/media/1",
                dateTaken = 1713976000000,
                mediaType = MediaType.Image,
                folderName = "Camera",
                size = 500000L,
                duration = null
            ),
            MediaItem(
                id = 2L,
                name = "video1.mp4",
                uri = "content://media/external/video/media/2",
                dateTaken = 1713977000000,
                mediaType = MediaType.Video,
                folderName = "Camera",
                size = 10500000L,
                duration = 60000L
            )
        )

        val albums = listOf(
            AlbumItem(
                id = "camera_folder_id",
                name = "Camera",
                mediaItems = mediaItems
            )
        )

        coEvery { mediaRepository.getAlbums() } returns flowOf(albums)

        // When
        val result = getAlbumsUseCase.execute()

        // Then

        assertEquals(1, result.first().size)
        val album = result.first().first()
        assertEquals("camera_folder_id", album.id)
        assertEquals("Camera", album.name)
        assertEquals(2, album.mediaCount)
        assertEquals("content://media/external/images/media/1", album.coverUri)

    }
}
