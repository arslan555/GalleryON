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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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
        val initialState = viewModel.screenState.value
        assertTrue(initialState.duplicates.isLoading)
        assertTrue(initialState.largeMedia.isLoading)
        assertTrue(initialState.oldMedia.isLoading)
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
        val state = viewModel.screenState.value
        assertEquals(1, state.duplicates.items.size)
        assertEquals(1, state.largeMedia.items.size)
        assertEquals(0, state.oldMedia.items.size)
        assertFalse(state.duplicates.isLoading)
        assertFalse(state.largeMedia.isLoading)
        assertFalse(state.oldMedia.isLoading)
    }

    @Test
    fun `toggleSelection adds and removes items correctly`() = runTest {
        // Test adding selection
        viewModel.toggleSelection(CleanerCategory.Duplicates, "duplicate1", true)
        assertEquals(setOf("duplicate1"), viewModel.selectedItems.value[CleanerCategory.Duplicates])
        
        // Test adding another selection
        viewModel.toggleSelection(CleanerCategory.Duplicates, "duplicate2", true)
        assertEquals(setOf("duplicate1", "duplicate2"), viewModel.selectedItems.value[CleanerCategory.Duplicates])
        
        // Test removing selection
        viewModel.toggleSelection(CleanerCategory.Duplicates, "duplicate1", false)
        assertEquals(setOf("duplicate2"), viewModel.selectedItems.value[CleanerCategory.Duplicates])
    }

    @Test
    fun `selectAll selects all items`() = runTest {
        val items = listOf(
            CleanupItem("1", emptyList(), CleanupType.Duplicates("hash1"), 1024L, 1),
            CleanupItem("2", emptyList(), CleanupType.Duplicates("hash2"), 2048L, 1)
        )
        
        viewModel.selectAll(CleanerCategory.Duplicates, items)
        assertEquals(setOf("1", "2"), viewModel.selectedItems.value[CleanerCategory.Duplicates])
    }

    @Test
    fun `should handle errors when loading cleanup items fails`() = runTest {
        // Given
        val errorMessage = "Failed to load duplicates"
        coEvery { findDuplicateImagesUseCase() } throws Exception(errorMessage)
        coEvery { findLargeVideosUseCase(100) } returns flowOf(emptyList())
        coEvery { findOldMediaUseCase(365) } returns flowOf(emptyList())
        
        // When
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.screenState.value
        assertEquals(errorMessage, state.duplicates.error)
        assertFalse(state.duplicates.isLoading)
        assertTrue(state.duplicates.items.isEmpty())
    }

    @Test
    fun `should clear error when retrying after failure`() = runTest {
        // Given - First attempt fails
        coEvery { findDuplicateImagesUseCase() } throws Exception("Initial error")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify error state
        assertEquals("Initial error", viewModel.screenState.value.duplicates.error)
        
        // When - Retry succeeds
        coEvery { findDuplicateImagesUseCase() } returns flowOf(emptyList())
        viewModel.onEvent(SmartCleanerEvent.ScanForDuplicates)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - Error should be cleared
        assertNull(viewModel.screenState.value.duplicates.error)
        assertFalse(viewModel.screenState.value.duplicates.isLoading)
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