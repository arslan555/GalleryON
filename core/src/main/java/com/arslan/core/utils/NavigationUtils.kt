package com.arslan.core.utils

import android.net.Uri

object NavigationUtils {

    fun encode(value: String): String {
        return Uri.encode(value)
    }
}
