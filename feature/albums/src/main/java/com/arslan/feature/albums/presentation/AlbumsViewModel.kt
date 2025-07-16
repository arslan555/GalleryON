package com.arslan.feature.albums.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arslan.domain.usecase.albums.GetAlbumsUseCase
import com.arslan.domain.usecase.albums.CreateAlbumUseCase
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
class AlbumsViewModel @Inject constructor(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val createAlbumUseCase: CreateAlbumUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AlbumsState>(AlbumsState.Loading)
    val state: StateFlow<AlbumsState> = _state.asStateFlow()

    private fun loadAlbums() {
        viewModelScope.launch(Dispatchers.IO) {
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
            is AlbumsEvent.CreateAlbum -> {
                viewModelScope.launch {
                    _state.value = AlbumsState.OperationLoading("Creating album...")
                    val success = createAlbumUseCase(event.name)
                    if (success) {
                        _state.value = AlbumsState.OperationSuccess("Album '${event.name}' created successfully!")
                        loadAlbums()
                    } else {
                        _state.value = AlbumsState.OperationError("Failed to create album '${event.name}'")
                    }
                }
            }
        }
    }
}
