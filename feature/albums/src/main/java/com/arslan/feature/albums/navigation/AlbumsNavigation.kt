package com.arslan.feature.albums.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.arslan.core.navigation.NavigationConstants
import com.arslan.feature.albums.presentation.AlbumsScreen

fun NavGraphBuilder.albumsNavigation(navController: NavHostController) {
    composable(NavigationConstants.ALBUMS_ROUTE) {
        AlbumsScreen(
            onAlbumClick = { album ->
                navController.navigate("media/${album.id}")
            }
        )
    }
}