package com.arslan.feature.albums.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.arslan.core.navigation.NavigationRoutes
import com.arslan.feature.albums.presentation.AlbumsScreen

fun NavGraphBuilder.albumsNavigation(navController: NavHostController) {
    composable(NavigationRoutes.ALBUMS) {
        AlbumsScreen(
            onAlbumClick = { album ->
                navController.navigate(NavigationRoutes.mediaRoute(album.id))
            }
        )
    }
}