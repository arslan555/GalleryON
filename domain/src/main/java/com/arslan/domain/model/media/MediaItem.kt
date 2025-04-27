package com.arslan.domain.model.media

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class MediaItem(
    val id: Long,
    val name: String,
    val uri: String,
    val dateTaken: Long?,
    val mediaType: MediaType,
    val folderName: String?,
    val size: Long?,
    val duration: Long? // Only relevant for videos
): Parcelable

@Parcelize
sealed interface MediaType: Parcelable {
    @Parcelize
    data object Image : MediaType
    @Parcelize
    data object Video : MediaType
}