package com.arslan.feature.albums.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arslan.domain.usecase.albums.GetAlbumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val getAlbumsUseCase: GetAlbumsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AlbumsState>(AlbumsState.Loading)
    val state: StateFlow<AlbumsState> = _state.asStateFlow()

    init {
        loadAlbums()
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            getAlbumsUseCase.execute()
                .onStart {
                    _state.value = AlbumsState.Loading
                }.catch { exception ->
                    _state.value = AlbumsState.Error(exception.message ?: "Unknown error")
                }
                .collect { albums ->
                    _state.value = AlbumsState.Success(albums)
                }
        }
    }

    fun onEvent(event: AlbumsEvent) {
        when (event) {
            AlbumsEvent.Refresh -> {
                loadAlbums()
            }

            is AlbumsEvent.AlbumClicked -> {
                _state.value = AlbumsState.AlbumSelected(event.albumId)
            }
        }
    }
}
