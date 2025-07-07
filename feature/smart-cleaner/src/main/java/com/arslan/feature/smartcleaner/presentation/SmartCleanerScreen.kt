package com.arslan.feature.smartcleaner.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.model.cleaner.CleanupType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartCleanerScreen(
    onNavigateBack: () -> Unit,
    viewModel: SmartCleanerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Cleaner") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(SmartCleanerEvent.Refresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (state) {
            is SmartCleanerState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is SmartCleanerState.Success -> {
                SmartCleanerContent(
                    state = state as SmartCleanerState.Success,
                    currentFilter = currentFilter,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            is SmartCleanerState.Error -> {
                ErrorContent(
                    message = (state as SmartCleanerState.Error).message,
                    onRetry = { viewModel.onEvent(SmartCleanerEvent.Refresh) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            is SmartCleanerState.Deleting -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Deleting selected items...")
                    }
                }
            }
            
            is SmartCleanerState.DeletionComplete -> {
                LaunchedEffect(state) {
                    // Show success message and refresh
                    viewModel.onEvent(SmartCleanerEvent.Refresh)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Successfully deleted ${(state as SmartCleanerState.DeletionComplete).deletedCount} items",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SmartCleanerContent(
    state: SmartCleanerState.Success,
    currentFilter: CleanupFilter,
    onEvent: (SmartCleanerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header with space saved info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Potential Space Savings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(state.totalSpaceSaved / (1024 * 1024))} MB",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onEvent(SmartCleanerEvent.SelectAll) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.SelectAll, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Select All")
            }
            
            OutlinedButton(
                onClick = { onEvent(SmartCleanerEvent.DeselectAll) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Clear, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear")
            }
        }

        // Delete button
        if (state.selectedItems.isNotEmpty()) {
            Button(
                onClick = { onEvent(SmartCleanerEvent.DeleteSelected) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Selected (${state.selectedItems.size})")
            }
        }

        // Filter buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                onClick = { onEvent(SmartCleanerEvent.Refresh) },
                label = { Text("All") },
                leadingIcon = {
                    Icon(Icons.Default.List, contentDescription = null)
                },
                selected = currentFilter is CleanupFilter.All,
                enabled = true,
                modifier = Modifier.weight(1f)
            )
            
            FilterChip(
                onClick = { onEvent(SmartCleanerEvent.ScanForDuplicates) },
                label = { Text("Duplicates") },
                leadingIcon = {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                },
                selected = currentFilter is CleanupFilter.Duplicates,
                enabled = true,
                modifier = Modifier.weight(1f)
            )
            
            FilterChip(
                onClick = { onEvent(SmartCleanerEvent.ScanForLargeVideos) },
                label = { Text("Large Videos") },
                leadingIcon = {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null)
                },
                selected = currentFilter is CleanupFilter.LargeVideos,
                enabled = true,
                modifier = Modifier.weight(1f)
            )
            
            FilterChip(
                onClick = { onEvent(SmartCleanerEvent.ScanForOldMedia) },
                label = { Text("Old Media") },
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                },
                selected = currentFilter is CleanupFilter.OldMedia,
                enabled = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cleanup items list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.cleanupItems) { cleanupItem ->
                CleanupItemCard(
                    cleanupItem = cleanupItem,
                    isSelected = state.selectedItems.contains(cleanupItem.id),
                    onSelectionChanged = { selected ->
                        onEvent(SmartCleanerEvent.ItemSelected(cleanupItem.id, selected))
                    }
                )
            }
        }
    }
}

@Composable
private fun CleanupItemCard(
    cleanupItem: CleanupItem,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cleanupItem.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${cleanupItem.itemCount} items • ${cleanupItem.sizeInMB}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                when (cleanupItem.cleanupType) {
                    is CleanupType.Duplicates -> Icons.Default.PhotoLibrary
                    is CleanupType.LargeVideos -> Icons.Default.VideoLibrary
                    is CleanupType.OldMedia -> Icons.Default.Schedule
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
} 