package com.arslan.domain.model.media

import org.junit.Assert.*


import org.junit.Assert.assertEquals
import org.junit.Test

class MediaItemTest {

    @Test
    fun `create MediaItem with correct properties`() {
        val mediaItem = MediaItem(
            id = 1L,
            name = "IMG_20240426.jpg",
            uri = "content://media/external/images/media/1",
            dateTaken = 1714123456000L,
            mediaType = MediaType.Image,
            folderName = "Camera",
            size = 2048L,
            duration = null
        )

        assertEquals(1L, mediaItem.id)
        assertEquals("IMG_20240426.jpg", mediaItem.name)
        assertEquals("content://media/external/images/media/1", mediaItem.uri)
        assertEquals(1714123456000L, mediaItem.dateTaken)
        assertEquals(MediaType.Image, mediaItem.mediaType)
        assertEquals("Camera", mediaItem.folderName)
        assertEquals(2048L, mediaItem.size)
        assertEquals(null, mediaItem.duration)
    }
}