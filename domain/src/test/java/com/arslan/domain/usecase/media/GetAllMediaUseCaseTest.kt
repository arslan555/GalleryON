package com.arslan.domain.usecase.media

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

class GetAllMediaUseCaseTest {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var getAllMediaUseCase: GetAllMediaUseCase

    @Before
    fun setup() {
        mediaRepository = mockk()
        getAllMediaUseCase = GetAllMediaUseCase(mediaRepository)
    }

    @Test
    fun `execute should return list of media items`() = runTest {
        // Given
        val mediaItems = listOf(
            MediaItem(
                id = 1L,
                name = "IMG_20240426.jpg",
                uri = "content://media/external/images/media/1",
                dateTaken = 1714123456000L,
                mediaType = MediaType.Image,
                folderName = "Camera",
                size = 2048L,
                duration = null
            ),
            MediaItem(
                id = 1L,
                name = "IMG_20240426.jpg",
                uri = "content://media/external/images/media/1",
                dateTaken = 1714123456000L,
                mediaType = MediaType.Image,
                folderName = "Camera",
                size = 2048L,
                duration = null
            ),
        )

        coEvery { mediaRepository.getAllMedia() } returns flowOf(mediaItems)

        // When
        val result = getAllMediaUseCase.execute()

        // Then
        result.collect { collectedItems ->
            assertEquals(mediaItems, collectedItems)
        }
    }
}