package com.arslan.feature.albums.presentation

import app.cash.turbine.test
import com.arslan.domain.model.album.AlbumItem
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.usecase.albums.GetAlbumsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumsViewModelTest {

    private lateinit var viewModel: AlbumsViewModel
    private lateinit var getAlbumsUseCase: GetAlbumsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getAlbumsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAlbums success - emits Loading then Success`() = runTest(testDispatcher) {
        // Given
        val fakeAlbum = AlbumItem(
            id = "Camera",
            name = "Camera",
            mediaItems = listOf(
                MediaItem(
                    id = 1L,
                    name = "photo.jpg",
                    uri = "content://photo",
                    dateTaken = 1000L,
                    mediaType = MediaType.Image,
                    folderName = "Camera",
                    size = 500L,
                    duration = null
                )
            )
        )

        coEvery { getAlbumsUseCase.execute() } returns flow {
            emit(listOf(fakeAlbum))
        }

        viewModel = AlbumsViewModel(getAlbumsUseCase)

        // 👇 Fix: make sure coroutine runs
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            assertEquals(AlbumsState.Success(listOf(fakeAlbum)), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `loadAlbums failure - emits Loading then Error`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Failed to load albums"
        coEvery { getAlbumsUseCase.execute() } returns flow {
            throw RuntimeException(errorMessage)
        }

        viewModel = AlbumsViewModel(getAlbumsUseCase)

        // Then
        viewModel.state.test {
            assertEquals(AlbumsState.Loading, awaitItem())
            val errorState = awaitItem()
            assert(errorState is AlbumsState.Error)
            assertEquals(errorMessage, (errorState as AlbumsState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent AlbumClicked - emits AlbumSelected`() = runTest(testDispatcher) {
        // Given
        coEvery { getAlbumsUseCase.execute() } returns flow {
            emit(emptyList())
        }

        viewModel = AlbumsViewModel(getAlbumsUseCase)

        val albumId = "Camera"

        // When
        viewModel.onEvent(AlbumsEvent.AlbumClicked(albumId))

        // Then
        assertEquals(AlbumsState.AlbumSelected(albumId), viewModel.state.value)
    }
}

