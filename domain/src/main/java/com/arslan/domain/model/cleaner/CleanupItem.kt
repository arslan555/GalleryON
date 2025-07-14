package com.arslan.domain.model.cleaner

import com.arslan.domain.model.media.MediaItem
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CleanupItem(
    val id: String,
    val mediaItems: List<MediaItem>,
    val cleanupType: CleanupType,
    val totalSize: Long,
    val itemCount: Int
) : Parcelable {
    val displayName: String
        get() = when (cleanupType) {
            is CleanupType.Duplicates -> "Duplicate Images (${itemCount})"
            is CleanupType.LargeVideos -> "Large Videos (${itemCount})"
            is CleanupType.OldMedia -> "Old Media (${itemCount})"
        }
    
    val sizeInMB: String
        get() = "${(totalSize / (1024 * 1024))} MB"
}

@Parcelize
sealed interface CleanupType : Parcelable {
    @Parcelize
    data class Duplicates(val hash: String) : CleanupType
    
    @Parcelize
    data class LargeVideos(val thresholdMB: Int) : CleanupType
    
    @Parcelize
    data class OldMedia(val daysOld: Int) : CleanupType
} 