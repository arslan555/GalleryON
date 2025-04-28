package com.arslan.data.repository

import app.cash.turbine.test
import com.arslan.data.media.MediaManager
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MediaRepositoryImplTest {

    private lateinit var mediaManager: MediaManager
    private lateinit var repository: MediaRepositoryImpl

    private val fakeMediaItems = listOf(
        MediaItem(
            id = 1L,
            name = "Photo1",
            uri = "content://photo1",
            dateTaken = 1234567890L,
            mediaType = MediaType.Image,
            folderName = "DCIM/Camera",
            size = 1024L,
            duration = null
        ),
        MediaItem(
            id = 2L,
            name = "Video1",
            uri = "content://video1",
            dateTaken = 1234567899L,
            mediaType = MediaType.Video,
            folderName = "DCIM/Videos",
            size = 2048L,
            duration = 60000L
        )
    )

    @Before
    fun setUp() {
        mediaManager = mockk(relaxed = true)
        coEvery { mediaManager.loadAllMedia() } returns Unit
        every { mediaManager.allMediaItems } returns MutableStateFlow(fakeMediaItems)
        repository = MediaRepositoryImpl(mediaManager)
    }

    @Test
    fun `getAllMediaItems returns media items from MediaManager`() = runTest {
        repository.getAllMediaItems().test {
            val items = awaitItem()

            assertEquals(2, items.size)
            assertEquals("Photo1", items[0].name)
            assertEquals("Video1", items[1].name)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
