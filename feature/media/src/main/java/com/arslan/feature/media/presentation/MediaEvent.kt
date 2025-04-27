package com.arslan.feature.media.presentation

sealed interface MediaEvent {
    data class LoadMedia(val albumId: String?) : MediaEvent
}