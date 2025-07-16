package com.arslan.feature.albums.presentation

sealed interface AlbumsEvent {
    data object Refresh : AlbumsEvent
    data class AlbumClicked(val albumId: String) : AlbumsEvent
    data class CreateAlbum(val name: String) : AlbumsEvent
}