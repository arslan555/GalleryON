package com.arslan.feature.smartcleaner.presentation

import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.model.cleaner.CleanupType
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.usecase.cleaner.DeleteMediaItemsUseCase
import com.arslan.domain.usecase.cleaner.FindDuplicateImagesUseCase
import com.arslan.domain.usecase.cleaner.FindLargeVideosUseCase
import com.arslan.domain.usecase.cleaner.FindOldMediaUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SmartCleanerViewModelTest {

    private lateinit var viewModel: SmartCleanerViewModel
    private lateinit var findDuplicateImagesUseCase: FindDuplicateImagesUseCase
    private lateinit var findLargeVideosUseCase: FindLargeVideosUseCase
    private lateinit var findOldMediaUseCase: FindOldMediaUseCase
    private lateinit var deleteMediaItemsUseCase: DeleteMediaItemsUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        findDuplicateImagesUseCase = mockk()
        findLargeVideosUseCase = mockk()
        findOldMediaUseCase = mockk()
        deleteMediaItemsUseCase = mockk()
        
        viewModel = SmartCleanerViewModel(
            findDuplicateImagesUseCase,
            findLargeVideosUseCase,
            findOldMediaUseCase,
            deleteMediaItemsUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading`() = runTest {
        val initialState = viewModel.state.value
        assertTrue(initialState is SmartCleanerState.Loading)
    }

    @Test
    fun `should load cleanup items successfully`() = runTest {
        // Given
        val mockCleanupItems = listOf(
            createMockCleanupItem("duplicate1", CleanupType.Duplicates("hash1"), 1024 * 1024L, 2),
            createMockCleanupItem("large_video1", CleanupType.LargeVideos(100), 50 * 1024 * 1024L, 1)
        )
        
        coEvery { findDuplicateImagesUseCase() } returns flowOf(listOf(mockCleanupItems[0]))
        coEvery { findLargeVideosUseCase(100) } returns flowOf(listOf(mockCleanupItems[1]))
        coEvery { findOldMediaUseCase(365) } returns flowOf(emptyList())
        
        // When
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.state.value
        assertTrue(state is SmartCleanerState.Success)
        assertEquals(2, (state as SmartCleanerState.Success).cleanupItems.size)
        assertEquals(51 * 1024 * 1024L, state.totalSpaceSaved)
    }

    @Test
    fun `should handle item selection correctly`() = runTest {
        // Given
        val mockCleanupItems = listOf(
            createMockCleanupItem("duplicate1", CleanupType.Duplicates("hash1"), 1024 * 1024L, 2)
        )
        
        coEvery { findDuplicateImagesUseCase() } returns flowOf(mockCleanupItems)
        coEvery { findLargeVideosUseCase(100) } returns flowOf(emptyList())
        coEvery { findOldMediaUseCase(365) } returns flowOf(emptyList())
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.onEvent(SmartCleanerEvent.ItemSelected("duplicate1", true))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.state.value
        assertTrue(state is SmartCleanerState.Success)
        assertEquals(1, (state as SmartCleanerState.Success).selectedItems.size)
        assertTrue(state.selectedItems.contains("duplicate1"))
    }

    @Test
    fun `should handle select all correctly`() = runTest {
        // Given
        val mockCleanupItems = listOf(
            createMockCleanupItem("duplicate1", CleanupType.Duplicates("hash1"), 1024 * 1024L, 2),
            createMockCleanupItem("large_video1", CleanupType.LargeVideos(100), 50 * 1024 * 1024L, 1)
        )
        
        coEvery { findDuplicateImagesUseCase() } returns flowOf(listOf(mockCleanupItems[0]))
        coEvery { findLargeVideosUseCase(100) } returns flowOf(listOf(mockCleanupItems[1]))
        coEvery { findOldMediaUseCase(365) } returns flowOf(emptyList())
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.onEvent(SmartCleanerEvent.SelectAll)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.state.value
        assertTrue(state is SmartCleanerState.Success)
        assertEquals(2, (state as SmartCleanerState.Success).selectedItems.size)
    }

    private fun createMockCleanupItem(
        id: String,
        cleanupType: CleanupType,
        totalSize: Long,
        itemCount: Int
    ): CleanupItem {
        val mockMediaItems = List(itemCount) { index ->
            MediaItem(
                id = index.toLong(),
                name = "test_media_$index",
                uri = "/test/path/$index",
                dateTaken = System.currentTimeMillis(),
                mediaType = MediaType.Image,
                folderName = "test_folder",
                size = totalSize / itemCount,
                duration = null
            )
        }
        
        return CleanupItem(
            id = id,
            mediaItems = mockMediaItems,
            cleanupType = cleanupType,
            totalSize = totalSize,
            itemCount = itemCount
        )
    }
} 