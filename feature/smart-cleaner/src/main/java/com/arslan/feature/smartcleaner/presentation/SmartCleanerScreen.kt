package com.arslan.feature.smartcleaner.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arslan.core.utils.FileUtils.formatSize
import com.arslan.domain.model.cleaner.CleanupItem
import com.arslan.domain.model.cleaner.CleanupType
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartCleanerScreen(
    onNavigateBack: () -> Unit,
    viewModel: SmartCleanerViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<CleanerCategory>(CleanerCategory.Duplicates) }

    // Calculate reclaimable storage
    val duplicateReclaimable = screenState.duplicates.items.sumOf { group ->
        group.mediaItems.drop(1).sumOf { it.size ?: 0L }
    }
    val largeVideosReclaimable = screenState.largeMedia.items.sumOf { item ->
        item.mediaItems.sumOf { it.size ?: 0L }
    }
    val oldMediaReclaimable = screenState.oldMedia.items.sumOf { item ->
        item.mediaItems.sumOf { it.size ?: 0L }
    }
    val totalReclaimable = duplicateReclaimable + largeVideosReclaimable + oldMediaReclaimable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Cleaner") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Potential Space to Free",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatSize(totalReclaimable),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Category Selection Chips
            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content based on selected category
            when (selectedCategory) {
                is CleanerCategory.Duplicates -> {
                    CategoryContent(
                        title = "Duplicates",
                        state = screenState.duplicates,
                        selectedItems = selectedItems[CleanerCategory.Duplicates] ?: emptySet(),
                        onSelectionChanged = { id, selected -> viewModel.toggleSelection(CleanerCategory.Duplicates, id, selected) },
                        onSelectAll = { viewModel.selectAll(CleanerCategory.Duplicates, screenState.duplicates.items) },
                        onClearAll = { viewModel.clearAll(CleanerCategory.Duplicates) },
                        onDeleteSelected = { viewModel.deleteSelected(CleanerCategory.Duplicates) },
                        onRetry = { viewModel.onEvent(SmartCleanerEvent.ScanForDuplicates) }
                    )
                }
                is CleanerCategory.LargeVideos -> {
                    CategoryContent(
                        title = "Large Videos",
                        state = screenState.largeMedia,
                        selectedItems = selectedItems[CleanerCategory.LargeVideos] ?: emptySet(),
                        onSelectionChanged = { id, selected -> viewModel.toggleSelection(CleanerCategory.LargeVideos, id, selected) },
                        onSelectAll = { viewModel.selectAll(CleanerCategory.LargeVideos, screenState.largeMedia.items) },
                        onClearAll = { viewModel.clearAll(CleanerCategory.LargeVideos) },
                        onDeleteSelected = { viewModel.deleteSelected(CleanerCategory.LargeVideos) },
                        onRetry = { viewModel.onEvent(SmartCleanerEvent.ScanForLargeVideos) }
                    )
                }
                is CleanerCategory.OldMedia -> {
                    CategoryContent(
                        title = "Old Media",
                        state = screenState.oldMedia,
                        selectedItems = selectedItems[CleanerCategory.OldMedia] ?: emptySet(),
                        onSelectionChanged = { id, selected -> viewModel.toggleSelection(CleanerCategory.OldMedia, id, selected) },
                        onSelectAll = { viewModel.selectAll(CleanerCategory.OldMedia, screenState.oldMedia.items) },
                        onClearAll = { viewModel.clearAll(CleanerCategory.OldMedia) },
                        onDeleteSelected = { viewModel.deleteSelected(CleanerCategory.OldMedia) },
                        onRetry = { viewModel.onEvent(SmartCleanerEvent.ScanForOldMedia) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChips(
    selectedCategory: CleanerCategory,
    onCategorySelected: (CleanerCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory is CleanerCategory.Duplicates,
            onClick = { onCategorySelected(CleanerCategory.Duplicates) },
            label = {
                if (selectedCategory is CleanerCategory.Duplicates) {
                    Text("Duplicates")
                }
            },
            leadingIcon = {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = "Duplicates",
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        FilterChip(
            selected = selectedCategory is CleanerCategory.LargeVideos,
            onClick = { onCategorySelected(CleanerCategory.LargeVideos) },
            label = {
                if (selectedCategory is CleanerCategory.LargeVideos) {
                    Text("Large Videos")
                }
            },
            leadingIcon = {
                Icon(
                    Icons.Default.VideoLibrary,
                    contentDescription = "Large Videos",
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        FilterChip(
            selected = selectedCategory is CleanerCategory.OldMedia,
            onClick = { onCategorySelected(CleanerCategory.OldMedia) },
            label = {
                if (selectedCategory is CleanerCategory.OldMedia) {
                    Text("Old Media")
                }
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Old Media",
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

@Composable
fun CategoryContent(
    title: String,
    state: CleanerCategoryState,
    selectedItems: Set<String>,
    onSelectionChanged: (String, Boolean) -> Unit,
    onSelectAll: () -> Unit,
    onClearAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onRetry: () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Selection and delete buttons row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onSelectAll,
            enabled = state.items.isNotEmpty() && state.error == null,
            modifier = Modifier.weight(1f)
        ) {
            Text("Select All")
        }
        OutlinedButton(
            onClick = onClearAll,
            enabled = selectedItems.isNotEmpty() && state.error == null,
            modifier = Modifier.weight(1f)
        ) {
            Text("Clear All")
        }
        Button(
            onClick = onDeleteSelected,
            enabled = selectedItems.isNotEmpty() && state.error == null,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.weight(1f)
        ) {
            Text("Delete (${selectedItems.size})")
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        state.error != null -> {
            ErrorContent(
                message = state.error,
                onRetry = onRetry,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }
        state.items.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) { item ->
                    CleanupItemCard(
                        cleanupItem = item,
                        isSelected = selectedItems.contains(item.id),
                        onSelectionChanged = { selected -> onSelectionChanged(item.id, selected) }
                    )
                }
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

@Preview(showBackground = true, name = "Smart Cleaner - New UI")
@Composable
private fun SmartCleanerNewUIPreview() {
    MaterialTheme {
        SmartCleanerScreen(
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Category Chips")
@Composable
private fun CategoryChipsPreview() {
    MaterialTheme {
        CategoryChips(
            selectedCategory = CleanerCategory.Duplicates,
            onCategorySelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Category Content - Loading")
@Composable
private fun CategoryContentLoadingPreview() {
    MaterialTheme {
        CategoryContent(
            title = "Duplicates",
            state = CleanerCategoryState(isLoading = true),
            selectedItems = emptySet(),
            onSelectionChanged = { _, _ -> },
            onSelectAll = {},
            onClearAll = {},
            onDeleteSelected = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Category Content - Success")
@Composable
private fun CategoryContentSuccessPreview() {
    MaterialTheme {
        CategoryContent(
            title = "Large Videos",
            state = CleanerCategoryState(
                isLoading = false,
                items = "large".createPreviewCleanupItems(CleanupType.LargeVideos(100), 2)
            ),
            selectedItems = setOf("large_1"),
            onSelectionChanged = { _, _ -> },
            onSelectAll = {},
            onClearAll = {},
            onDeleteSelected = {},
            onRetry = {}
        )
    }
}

// Helper function for previews
private fun String.createPreviewCleanupItems(
    cleanupType: CleanupType,
    count: Int
): List<CleanupItem> {
    return List(count) { index ->
        createPreviewCleanupItem(
            id = "${this}_${index + 1}",
            cleanupType = cleanupType,
            totalSize = (100 + index * 50) * 1024 * 1024L,
            itemCount = index + 1
        )
    }
}

private fun createPreviewCleanupItem(
    id: String,
    cleanupType: CleanupType,
    totalSize: Long,
    itemCount: Int
): CleanupItem {
    val mockMediaItems = List(itemCount) { index ->
        MediaItem(
            id = index.toLong(),
            name = "preview_media_$index",
            uri = "/preview/path/$index",
            dateTaken = System.currentTimeMillis() - (index * 24 * 60 * 60 * 1000L),
            mediaType = MediaType.Image,
            folderName = "preview_folder",
            size = totalSize / itemCount,
            duration = null
        )
    }

    return CleanupItem(
        id = id,
        mediaItems = mockMediaItems,
        cleanupType = cleanupType,
        totalSize = totalSize,
        itemCount = itemCount
    )
}

