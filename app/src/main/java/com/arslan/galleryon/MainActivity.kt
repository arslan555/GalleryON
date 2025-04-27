package com.arslan.galleryon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.arslan.core.ui.theme.GalleryOnTheme
import com.arslan.galleryon.navigation.GalleryNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            GalleryOnTheme {
                val navController = rememberNavController()
                GalleryNavGraph(navController = navController)
            }
        }
    }
}