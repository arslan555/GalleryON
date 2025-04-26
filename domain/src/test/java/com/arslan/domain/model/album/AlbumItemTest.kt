package com.arslan.domain.model.album

import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import org.junit.Assert.*
import org.junit.Test

class AlbumItemTest {

    @Test
    fun `create AlbumItem correctly and validate properties`() {
        val mediaItems = listOf(
            MediaItem(
                id = 1L,
                name = "image1.jpg",
                uri = "content://media/external/images/media/1",
                dateTaken = 1713976000000,
                mediaType = MediaType.Image,
                folderName = "Camera",
                size = 500000L,
                duration = null
            ),
            MediaItem(
                id = 2L,
                name = "video1.mp4",
                uri = "content://media/external/video/media/2",
                dateTaken = 1713977000000,
                mediaType = MediaType.Video,
                folderName = "Camera",
                size = 10500000L,
                duration = 60000L
            )
        )

        val albumItem = AlbumItem(
            id = "camera_folder_id",
            name = "Camera",
            mediaItems = mediaItems
        )

        assertEquals("camera_folder_id", albumItem.id)
        assertEquals("Camera", albumItem.name)
        assertEquals(2, albumItem.mediaCount)
        assertEquals("content://media/external/images/media/1", albumItem.coverUri)
    }
}