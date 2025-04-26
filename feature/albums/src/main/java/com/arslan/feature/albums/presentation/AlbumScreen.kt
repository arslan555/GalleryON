package com.arslan.feature.albums.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arslan.domain.model.album.AlbumItem
import com.arslan.feature.albums.presentation.components.AlbumGridItem
import com.arslan.feature.albums.presentation.components.AlbumListItem

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel = hiltViewModel(),
    onAlbumClick: (AlbumItem) -> Unit
) {
    val state by viewModel.state.collectAsState()

    var isGrid by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Albums") },
                actions = {
                    IconButton(onClick = { isGrid = !isGrid }) {
                        Icon(
                            imageVector = if (isGrid) Icons.Filled.List else Icons.Filled.ViewModule,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        when (val currentState = state) {
            is AlbumsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AlbumsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = currentState.message)
                }
            }

            is AlbumsState.Success -> {
                val albums = currentState.albums

                if (isGrid) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        items(albums) { album ->
                            AlbumGridItem(album = album, onClick = {
                                viewModel.onEvent(AlbumsEvent.AlbumClicked(album.id))
                                onAlbumClick(album)
                            })
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        items(albums) { album ->
                            AlbumListItem(album = album, onClick = {
                                viewModel.onEvent(AlbumsEvent.AlbumClicked(album.id))
                                onAlbumClick(album)
                            })
                        }
                    }
                }
            }

            is AlbumsState.AlbumSelected -> {
                // Optionally handle selected album
            }
        }
    }
}