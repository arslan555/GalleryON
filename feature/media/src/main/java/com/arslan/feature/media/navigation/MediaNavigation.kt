package com.arslan.feature.media.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.arslan.core.navigation.NavigationRoutes
import com.arslan.core.utils.NavigationUtils
import com.arslan.domain.model.media.MediaItem
import com.arslan.feature.media.presentation.MediaScreen
import com.arslan.feature.media.presentation.mediadetail.MediaDetailScreen
import com.arslan.feature.media.presentation.mediadetail.MediaDetailViewModel
import com.arslan.feature.media.utils.shareMediaItem


fun NavGraphBuilder.mediaNavigation(navController: NavHostController) {
    composable(
        route = "${NavigationRoutes.MEDIA}/{albumId}",
        arguments = listOf(
            navArgument("albumId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val albumId = backStackEntry.arguments?.getString("albumId") ?: ""

        MediaScreen(
            albumId = albumId,
            onBackClick = { navController.popBackStack() },
            onMediaClick = { mediaItem ->
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("mediaItem", mediaItem)
                navController.navigate("${NavigationRoutes.MEDIA_DETAIL}/${mediaItem.id}")
            }
        )
    }

    composable(
        route = "${NavigationRoutes.MEDIA_DETAIL}/{mediaId}",
        arguments = listOf(navArgument("mediaId") { type = NavType.LongType })
    ) { backStackEntry ->
        val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: return@composable
        val context = LocalContext.current
        val viewModel: MediaDetailViewModel = hiltViewModel()
        val mediaItem by viewModel.getMediaItem(mediaId).collectAsState(initial = null)
        mediaItem?.let {
            MediaDetailScreen(
                mediaItem = it,
                onBackClick = { navController.popBackStack() },
                onShareClick = { context.shareMediaItem(it)}
            )
        }
    }
}
