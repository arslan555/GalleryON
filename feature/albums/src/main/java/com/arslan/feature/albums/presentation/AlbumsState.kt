package com.arslan.feature.albums.presentation

import com.arslan.domain.model.album.AlbumItem

sealed interface AlbumsState {
    data object Loading : AlbumsState
    data class Success(val albums: List<AlbumItem>) : AlbumsState
    data class Error(val message: String) : AlbumsState
    data class AlbumSelected(val albumId: String) : AlbumsState
    data class OperationLoading(val operation: String) : AlbumsState
    data class OperationSuccess(val message: String) : AlbumsState
    data class OperationError(val message: String) : AlbumsState
}