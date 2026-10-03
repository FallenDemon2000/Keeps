package com.example.keeps.data.media

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns

/**
 * Reads [Photo] (size, pixel dimensions, mime type) for `content://` URIs returned by the system photo
 * picker, via [ContentResolver] queries. No `READ_MEDIA_IMAGES`/
 * `READ_EXTERNAL_STORAGE` permission is required: the picker grants temporary,
 * per-URI read access to the caller.
 */
class PhotoDataSource(
    private val contentResolver: ContentResolver,
) {

    fun readPhoto(uri: Uri): Photo {
        var sizeBytes = 0L
        var width = 0
        var height = 0

        val projection = arrayOf(
            OpenableColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
        )
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val widthIndex = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH)
                val heightIndex = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT)
                if (sizeIndex >= 0) sizeBytes = cursor.getLong(sizeIndex)
                if (widthIndex >= 0) width = cursor.getInt(widthIndex)
                if (heightIndex >= 0) height = cursor.getInt(heightIndex)
            }
        }

        return Photo(
            id = uri.toString(),
            uri = uri,
            sizeBytes = sizeBytes,
            width = width,
            height = height,
            mimeType = contentResolver.getType(uri),
        )
    }
}
