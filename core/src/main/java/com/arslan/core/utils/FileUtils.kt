package com.arslan.core.utils

import android.content.Context
import android.media.browse.MediaBrowser
import java.util.Locale
object FileUtils {
     fun readableFileSize(size: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var sizeInUnit = size.toDouble()
        var unitIndex = 0
        while (sizeInUnit >= 1024 && unitIndex < units.lastIndex) {
            sizeInUnit /= 1024
            unitIndex++
        }
        return String.format(Locale.getDefault(), "%.1f %s", sizeInUnit, units[unitIndex])
    }

     fun formatDuration(durationMillis: Long): String {
        val seconds = durationMillis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%d:%02d".format(minutes, remainingSeconds)
    }
}