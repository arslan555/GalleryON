package com.arslan.domain.usecase.albums
import app.cash.turbine.test
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAlbumsUseCaseTest {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var getAlbumsUseCase: GetAlbumsUseCase

    private val fakeMediaItems = listOf(
        MediaItem(
            id = 1L,
            name = "Image1",
            uri = "content://image1",
            dateTaken = 1620000000L,
            mediaType = MediaType.Image,
            folderName = "DCIM/Camera",
            size = 1024,
            duration = null
        ),
        MediaItem(
            id = 2L,
            name = "Video1",
            uri = "content://video1",
            dateTaken = 1630000000L,
            mediaType = MediaType.Video,
            folderName = "DCIM/Videos",
            size = 2048,
            duration = 60000L
        )
    )

    @Before
    fun setUp() {
        mediaRepository = mockk()
        coEvery { mediaRepository.getAllMediaItems() } returns flowOf(fakeMediaItems)
        getAlbumsUseCase = GetAlbumsUseCase(mediaRepository)
    }

    @Test
    fun `execute returns list of albums grouped by folderName`() = runTest {
        getAlbumsUseCase.execute().test {
            val albums = awaitItem()

            assertEquals(2, albums.size)

            val firstAlbum = albums[0]
            val secondAlbum = albums[1]

            // Highest dateTaken comes first
            assertEquals("Videos", firstAlbum.name)
            assertEquals(1, firstAlbum.mediaItems.size)

            assertEquals("Camera", secondAlbum.name)
            assertEquals(1, secondAlbum.mediaItems.size)

            awaitComplete()
        }
    }
}