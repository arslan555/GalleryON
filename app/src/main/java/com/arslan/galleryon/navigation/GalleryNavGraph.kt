package com.arslan.galleryon.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.arslan.core.navigation.NavigationRoutes
import com.arslan.feature.albums.navigation.albumsNavigation
import com.arslan.feature.media.navigation.mediaNavigation


@Composable
fun GalleryNavGraph(
    navController: NavHostController,
    startDestination: String = NavigationRoutes.ALBUMS
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        albumsNavigation(navController)
        mediaNavigation(navController)
    }
}