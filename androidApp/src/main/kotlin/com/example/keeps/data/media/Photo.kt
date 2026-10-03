package com.example.keeps.data.media

import android.net.Uri

/**
 * A single photo selected by the user via the device photo picker, along with
 * the metadata read from [android.content.ContentResolver] (size, dimensions,
 * mime type). This is the domain representation; see `PhotoUi` for the
 * presentation-layer mapping used by the Results screen.
 */
data class Photo(
    val id: String,
    val uri: Uri,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val mimeType: String?,
)
