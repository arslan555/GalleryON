package com.arslan.feature.media.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arslan.domain.usecase.media.GetMediaItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val getMediaItemsUseCase: GetMediaItemsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MediaState>(MediaState.Loading)
    val state: StateFlow<MediaState> = _state.asStateFlow()

    fun onEvent(event: MediaEvent) {
        when (event) {
            is MediaEvent.LoadMedia -> loadMedia(event.albumId)
        }
    }

    private fun loadMedia(albumId: String?) {
        _state.value = MediaState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            getMediaItemsUseCase.execute(albumId)
                .onStart {
                    _state.value = MediaState.Loading
                }
                .catch { exception ->
                    _state.value = MediaState.Error(exception.message ?: "Unknown error")
                }
                .collect { mediaItems ->
                    _state.value = MediaState.Success(mediaItems)
                }
        }
    }
}