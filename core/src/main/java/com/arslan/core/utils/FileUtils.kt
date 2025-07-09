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

    fun formatSize(bytes: Long): String {
        val kb = 1024L
        val mb = kb * 1024
        val gb = mb * 1024
        return when {
            bytes >= gb -> "%.2f GB".format(bytes.toDouble() / gb)
            bytes >= mb -> "%.2f MB".format(bytes.toDouble() / mb)
            bytes >= kb -> "%.2f KB".format(bytes.toDouble() / kb)
            else -> "$bytes B"
        }
    }
}