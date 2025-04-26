package com.arslan.feature.media.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.arslan.core.navigation.NavigationConstants
import com.arslan.feature.media.presentation.MediaScreen

fun NavGraphBuilder.mediaNavigation(navController: NavHostController) {
    composable(NavigationConstants.MEDIA_ROUTE) { backStackEntry ->
        val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
        MediaScreen(
            albumId = albumId,
            onMediaClick = { mediaId ->
                navController.navigate("mediaDetail/$mediaId")
            }
        )
    }
}