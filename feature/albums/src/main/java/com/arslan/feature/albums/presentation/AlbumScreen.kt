package com.arslan.feature.albums.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arslan.core.permissions.RequestPermissions
import com.arslan.domain.model.album.AlbumItem
import com.arslan.feature.albums.presentation.components.AlbumGridItem
import com.arslan.feature.albums.presentation.components.AlbumListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel = hiltViewModel(),
    onAlbumClick: (AlbumItem) -> Unit,
    onSmartCleanerClick: () -> Unit
) {
    var isGrid by rememberSaveable { mutableStateOf(true) }
    var hasPermission by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var albumName by remember { mutableStateOf("") }
    
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Albums") },
                actions = {
                    IconButton(onClick = onSmartCleanerClick) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Smart Cleaner"
                        )
                    }
                    IconButton(onClick = { isGrid = !isGrid }) {
                        Icon(
                            imageVector = if (isGrid) Icons.AutoMirrored.Filled.List else Icons.Filled.ViewModule,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    albumName = ""
                    showCreateDialog = true 
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Album")
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            RequestPermissions { granted ->
                hasPermission = granted
                if (granted) {
                    viewModel.onEvent(AlbumsEvent.Refresh)
                }
            }

            if (!hasPermission) {
                Text(
                    text = "Storage permissions are required to access albums.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                when (val currentState = state) {
                    is AlbumsState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is AlbumsState.Error -> {
                        Text(
                            text = currentState.message,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is AlbumsState.OperationLoading -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentState.operation,
                                style = MaterialTheme.typography.bodyLarge
                            )
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
                                modifier = Modifier.fillMaxSize()
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
                                modifier = Modifier.fillMaxSize()
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

                    is AlbumsState.OperationSuccess -> {
                        // Show success state briefly, then load albums
                        LaunchedEffect(currentState) {
                            kotlinx.coroutines.delay(1500)
                            viewModel.onEvent(AlbumsEvent.Refresh)
                        }
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentState.message,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    is AlbumsState.OperationError -> {
                        // Show error state briefly, then load albums
                        LaunchedEffect(currentState) {
                            kotlinx.coroutines.delay(2000)
                            viewModel.onEvent(AlbumsEvent.Refresh)
                        }
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentState.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Album Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New Album") },
            text = {
                OutlinedTextField(
                    value = albumName,
                    onValueChange = { albumName = it },
                    label = { Text("Album Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (albumName.isNotBlank()) {
                            viewModel.onEvent(AlbumsEvent.CreateAlbum(albumName))
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
