package com.arslan.feature.smartcleaner.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.arslan.core.navigation.NavigationRoutes
import com.arslan.feature.smartcleaner.presentation.SmartCleanerScreen

fun NavGraphBuilder.smartCleanerNavigation(navController: NavController) {
    composable(NavigationRoutes.SMART_CLEANER) {
        SmartCleanerScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
} 