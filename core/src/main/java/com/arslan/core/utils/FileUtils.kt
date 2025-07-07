package com.arslan.core.utils

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

    fun formatSpaceSavings(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        val gb = bytes / (1024.0 * 1024.0 * 1024.0)
        return if (gb >= 1) {
            String.format(Locale.getDefault(), "%.2f GB", gb)
        } else {
            String.format(Locale.getDefault(), "%.2f MB", mb)
        }
    }
}