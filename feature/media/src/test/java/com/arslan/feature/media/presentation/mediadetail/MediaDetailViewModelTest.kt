package com.arslan.feature.media.presentation.mediadetail

import app.cash.turbine.test
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.usecase.mediadetails.GetMediaDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MediaDetailViewModelTest {

    private lateinit var viewModel: MediaDetailViewModel
    private lateinit var getMediaDetailUseCase: GetMediaDetailUseCase

    @Before
    fun setUp() {
        getMediaDetailUseCase = mockk()
        viewModel = MediaDetailViewModel(getMediaDetailUseCase)
    }

    @Test
    fun `getMediaItem emits expected media item`() = runTest {
        // Given
        val mediaId = 1L
        val fakeMediaItem = MediaItem(
            id = mediaId,
            name = "photo1.jpg",
            uri = "content://photo1",
            dateTaken = 1000L,
            mediaType = MediaType.Image,
            folderName = "Camera",
            size = 2048L,
            duration = null
        )
        coEvery { getMediaDetailUseCase.execute(mediaId) } returns flowOf(fakeMediaItem)

        // When + Then
        viewModel.getMediaItem(mediaId).test {
            val item = awaitItem()
            assertEquals(fakeMediaItem, item)
            awaitComplete()
        }
    }

    @Test
    fun `getMediaItem emits null when item not found`() = runTest {
        // Given
        val mediaId = 99L
        coEvery { getMediaDetailUseCase.execute(mediaId) } returns flowOf(null)

        // When + Then
        viewModel.getMediaItem(mediaId).test {
            val item = awaitItem()
            assertEquals(null, item)
            awaitComplete()
        }
    }
}
