package com.arslan.galleryon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.arslan.core.ui.theme.GalleryOnTheme
import com.arslan.data.cleaner.MediaDeletionHelper
import com.arslan.galleryon.navigation.GalleryNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mediaDeletionHelper: MediaDeletionHelper
    
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

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // Handle MediaStore delete request result
        if (requestCode == MediaDeletionHelper.DELETE_REQUEST_CODE) {
            // Handle the result through MediaDeletionHelper
            val success = mediaDeletionHelper.handleDeleteResult(requestCode, resultCode)
            val result = if (success) "SUCCESS" else "CANCELLED"
            Log.d("MainActivity", "MediaStore delete request result: $result")
        }
    }
}