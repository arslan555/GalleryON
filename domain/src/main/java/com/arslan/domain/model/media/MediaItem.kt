package com.arslan.domain.model.media

data class MediaItem(
    val id: Long,
    val name: String,
    val uri: String,
    val dateTaken: Long?,
    val mediaType: MediaType,
    val folderName: String?,
    val size: Long?,
    val duration: Long? // Only relevant for videos
)

// Sealed class instead of enum for better control and extension
sealed interface MediaType {
    data object Image : MediaType
    data object Video : MediaType
}