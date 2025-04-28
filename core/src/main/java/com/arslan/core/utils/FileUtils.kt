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
}