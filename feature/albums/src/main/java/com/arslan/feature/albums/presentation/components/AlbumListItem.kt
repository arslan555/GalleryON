package com.arslan.feature.albums.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arslan.domain.model.album.AlbumItem

@Composable
fun AlbumListItem(
    album: AlbumItem,
    onClick: () -> Unit
) {
    AlbumCardItem(
        album = album,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        onClick = onClick
    )
}
