package com.arslan.feature.media.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arslan.domain.model.media.MediaItem
import com.arslan.feature.media.presentation.component.MediaItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    albumId: String?, // Album ID to filter media
    onBackClick: () -> Unit,
    onMediaClick: (MediaItem) -> Unit,
    viewModel: MediaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(albumId) {
        viewModel.onEvent(MediaEvent.LoadMedia(albumId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Media") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is MediaState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is MediaState.Error -> {
                    Text(
                        text = currentState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is MediaState.Success -> {
                    val mediaItems = currentState.mediaItems

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mediaItems) { mediaItem ->
                            MediaItemCard(
                                mediaItem = mediaItem,
                                onClick = { onMediaClick(mediaItem) }
                            )
                        }
                    }
                }
            }
        }
    }
}
