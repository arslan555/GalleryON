package com.arslan.feature.albums.presentation

import app.cash.turbine.test
import com.arslan.domain.model.album.AlbumItem
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.usecase.albums.GetAlbumsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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
        Dispatchers.resetMain() // 🧹 Always reset after test
    }

    @Test
    fun `initial load success - emits Loading then Success`() = runTest {
        val albums = listOf(
            AlbumItem(
                id = "1",
                name = "Camera",
                mediaItems = listOf(
                    MediaItem(
                        id = 1L,
                        name = "image1.jpg",
                        uri = "content://media/external/images/media/1",
                        dateTaken = 1713976000000,
                        mediaType = MediaType.Image,
                        folderName = "Camera",
                        size = 500000L,
                        duration = null
                    )
                )
            )
        )
        coEvery { getAlbumsUseCase.execute() } returns kotlinx.coroutines.flow.flow { emit(albums) }

        viewModel = AlbumsViewModel(getAlbumsUseCase)

        viewModel.state.test {
            assertEquals(AlbumsState.Loading, awaitItem())
            assertEquals(AlbumsState.Success(albums), awaitItem())
        }
    }

    @Test
    fun `initial load failure - emits Loading then Error`() = runTest {
        val errorMessage = "Something went wrong"

        // 👇 Flow itself throws error on collect
        coEvery { getAlbumsUseCase.execute() } returns flow {
            throw RuntimeException(errorMessage)
        }

        viewModel = AlbumsViewModel(getAlbumsUseCase)

        viewModel.state.test {
            assertEquals(AlbumsState.Loading, awaitItem())
            val errorState = awaitItem()
            assert(errorState is AlbumsState.Error)
            assertEquals(errorMessage, (errorState as AlbumsState.Error).message)
        }
    }

    @Test
    fun `on Refresh event - emits Loading then Success`() = runTest {
        val albums = listOf(
            AlbumItem(id = "1", name = "Camera", mediaItems = emptyList())
        )
        coEvery { getAlbumsUseCase.execute() } returns kotlinx.coroutines.flow.flow { emit(albums) }

        viewModel = AlbumsViewModel(getAlbumsUseCase)

        viewModel.state.test {
            awaitItem() // Loading
            awaitItem() // Success

            // Now simulate refresh
            viewModel.onEvent(AlbumsEvent.Refresh)

            assertEquals(AlbumsState.Loading, awaitItem())
            assertEquals(AlbumsState.Success(albums), awaitItem())
        }
    }

    @Test
    fun `on AlbumClicked event - emits AlbumSelected state`() = runTest {
        coEvery { getAlbumsUseCase.execute() } returns kotlinx.coroutines.flow.flow { emit(emptyList()) }

        viewModel = AlbumsViewModel(getAlbumsUseCase)

        val albumId = "123"

        viewModel.state.test {
            awaitItem() // Loading
            awaitItem() // Success
            viewModel.onEvent(AlbumsEvent.AlbumClicked(albumId))
            assertEquals(AlbumsState.AlbumSelected(albumId), awaitItem())
        }
    }
}

