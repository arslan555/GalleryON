package com.arslan.feature.albums.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arslan.domain.model.album.AlbumItem

@Composable
fun AlbumGridItem(
    album: AlbumItem,
    onClick: () -> Unit
) {
    AlbumCardItem(
        album = album,
        modifier = Modifier
            .padding(4.dp),
        onClick = onClick
    )
}
