package com.arslan.domain.usecase.mediadetails

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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetMediaDetailUseCaseTest {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var getMediaDetailUseCase: GetMediaDetailUseCase

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
        getMediaDetailUseCase = GetMediaDetailUseCase(mediaRepository)
    }

    @Test
    fun `execute returns correct MediaItem when ID exists`() = runTest {
        getMediaDetailUseCase.execute(2L).test {
            val result = awaitItem()

            assertEquals("Video1", result?.name)
            assertEquals(2L, result?.id)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns null when ID does not exist`() = runTest {
        getMediaDetailUseCase.execute(999L).test {
            val result = awaitItem()

            assertNull(result)
            awaitComplete()
        }
    }
}
