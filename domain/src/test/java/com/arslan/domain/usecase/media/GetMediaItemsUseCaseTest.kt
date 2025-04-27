package com.arslan.domain.usecase.media
import app.cash.turbine.test
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMediaItemsUseCaseTest {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var getMediaItemsUseCase: GetMediaItemsUseCase

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
        getMediaItemsUseCase = GetMediaItemsUseCase(mediaRepository)
    }

    @Test
    fun `execute returns filtered and sorted media items by albumId`() = runTest {
        getMediaItemsUseCase.execute("DCIM/Camera").test {
            val result = awaitItem()

            assertEquals(1, result.size)
            assertEquals("Image1", result[0].name)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns empty list if albumId does not match`() = runTest {
        getMediaItemsUseCase.execute("DCIM/UnknownFolder").test {
            val result = awaitItem()

            assertEquals(0, result.size)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns all media when albumId is null`() = runTest {
        getMediaItemsUseCase.execute(null).test {
            val result = awaitItem()

            assertEquals(2, result.size)
            awaitComplete()
        }
    }
}
