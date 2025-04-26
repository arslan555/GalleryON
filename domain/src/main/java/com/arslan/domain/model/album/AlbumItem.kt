package com.arslan.domain.model.album

import com.arslan.domain.model.media.MediaItem

data class AlbumItem(
    val id: String,                // Folder path or unique ID
    val name: String,              // Folder display name
    val mediaItems: List<MediaItem> // List of media items inside the album
) {
    val mediaCount: Int
        get() = mediaItems.size

    val coverUri: String?
        get() = mediaItems.firstOrNull()?.uri
}