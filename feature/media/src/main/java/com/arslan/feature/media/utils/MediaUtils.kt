package com.arslan.feature.media.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.arslan.domain.model.media.MediaItem


fun Context.shareMediaItem(mediaItem: MediaItem) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*" // or "video/*" if you want to be smarter by checking MediaType
        putExtra(Intent.EXTRA_STREAM, Uri.parse(mediaItem.uri))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(shareIntent, "Share Media via"))
}