package com.arslan.galleryon.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.arslan.core.navigation.NavigationConstants
import com.arslan.feature.albums.navigation.albumsNavigation
import com.arslan.feature.media.navigation.mediaNavigation

// import other screens...

@Composable
fun GalleryNavGraph(
    navController: NavHostController,
    startDestination: String = NavigationConstants.ALBUMS_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        albumsNavigation(navController)
        mediaNavigation(navController)
//        mediaDetailNav(navController)
    }
}