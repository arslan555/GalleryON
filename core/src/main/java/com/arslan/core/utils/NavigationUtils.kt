package com.arslan.core.utils

import android.net.Uri
import androidx.navigation.NavController

object NavigationUtils {

    fun encode(value: String): String {
        return Uri.encode(value)
    }

    fun decode(value: String): String {
        return Uri.decode(value)
    }
}


fun NavController.safeNavigate(route: String) {
    try {
        this.navigate(route)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}