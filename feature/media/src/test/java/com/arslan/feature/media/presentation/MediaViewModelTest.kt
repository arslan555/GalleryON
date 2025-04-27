package com.arslan.feature.media.presentation

import app.cash.turbine.test
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.usecase.media.GetMediaItemsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaViewModelTest {

    private lateinit var viewModel: MediaViewModel
    private lateinit var getMediaItemsUseCase: GetMediaItemsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getMediaItemsUseCase = mockk()
        viewModel = MediaViewModel(getMediaItemsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        assert(viewModel.state.value is MediaState.Loading)
    }

    @Test
    fun `on LoadMedia event success - emits Loading then Success`() = runTest {
        // Given
        val fakeMediaList = listOf(
            MediaItem(
                id = 1L,
                name = "photo1.jpg",
                uri = "content://photo1",
                dateTaken = 1000L,
                mediaType = MediaType.Image,
                folderName = "Camera",
                size = 2048L,
                duration = null
            )
        )
        coEvery { getMediaItemsUseCase.execute(any()) } returns flow {
            emit(fakeMediaList)
        }

        // When
        viewModel.onEvent(MediaEvent.LoadMedia(albumId = "Camera"))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            assertEquals(MediaState.Loading, awaitItem())
            assertEquals(MediaState.Success(fakeMediaList), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `on LoadMedia event error - emits Loading then Error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getMediaItemsUseCase.execute(any()) } returns flow {
            throw RuntimeException(errorMessage)
        }

        // When
        viewModel.onEvent(MediaEvent.LoadMedia(albumId = "Camera"))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            assertEquals(MediaState.Loading, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is MediaState.Error)
            assertEquals(errorMessage, (errorState as MediaState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
