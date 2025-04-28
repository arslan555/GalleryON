package com.arslan.core.utils

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatDate(pattern: String = "dd MMM yyyy"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}