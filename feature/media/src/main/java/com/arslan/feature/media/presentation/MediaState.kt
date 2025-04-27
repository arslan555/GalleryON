package com.arslan.feature.media.presentation

import com.arslan.domain.model.media.MediaItem

sealed interface MediaState {
    data object Loading : MediaState
    data class Success(val mediaItems: List<MediaItem>) : MediaState
    data class Error(val message: String) : MediaState
}