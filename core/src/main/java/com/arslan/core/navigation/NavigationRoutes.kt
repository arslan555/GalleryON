package com.arslan.core.navigation

import com.arslan.core.utils.NavigationUtils

object NavigationRoutes {
    const val ALBUMS = "albums_screen"
    const val MEDIA = "media_screen"
    const val MEDIA_DETAIL = "media_detail_screen"
    const val SMART_CLEANER = "smart_cleaner_screen"
    fun mediaRoute(albumId: String): String = "$MEDIA/${NavigationUtils.encode(albumId)}"
}