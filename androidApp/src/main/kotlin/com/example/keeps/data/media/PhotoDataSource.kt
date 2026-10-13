package com.example.keeps.data.media

import android.content.ContentResolver
import android.content.ContentUris
import android.content.IntentSender
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.example.keeps.ktx.runOrNull

/**
 * Reads [Photo] (size, pixel dimensions, mime type) for `content://` URIs returned by the system photo
 * picker, via [ContentResolver] queries. No `READ_MEDIA_IMAGES`/
 * `READ_EXTERNAL_STORAGE` permission is required: the picker grants temporary,
 * per-URI read access to the caller. Deletion goes through
 * [MediaStore.createDeleteRequest] (system confirmation dialog), which needs
 * real MediaStore URIs and therefore the read-images permission.
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

    /**
     * Photo-picker URIs can't be deleted directly, so each one is first resolved
     * to its real MediaStore URI (requires the read-images permission).
     * @throws IllegalArgumentException if a photo can't be resolved.
     */
    fun createDeleteRequest(uris: List<Uri>): IntentSender {
        val mediaUris = uris.map { uri ->
            resolveMediaStoreUri(uri)
                ?: throw IllegalArgumentException("Cannot resolve MediaStore URI for $uri")
        }

        return MediaStore.createDeleteRequest(contentResolver, mediaUris).intentSender
    }

    private fun resolveMediaStoreUri(uri: Uri): Uri? {
        if (uri.authority == MediaStore.AUTHORITY && !isPickerUri(uri)) return uri

        val (name, size) = readNameAndSize(uri)
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        // Picker URIs usually end with the MediaStore id; verify before trusting it.
        uri.lastPathSegment?.toLongOrNull()?.let { id ->
            val candidate = ContentUris.withAppendedId(collection, id)
            if (exists(candidate)) return candidate
        }

        if (name == null) return null
        val projection = arrayOf(MediaStore.Images.Media._ID, OpenableColumns.SIZE)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(name)

        contentResolver.query(collection, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    if (size > 0L && cursor.getLong(1) != size) continue
                    return ContentUris.withAppendedId(collection, cursor.getLong(0))
                }
            }
        return null
    }

    private fun isPickerUri(uri: Uri) = uri.pathSegments.firstOrNull() == "picker"

    private fun exists(uri: Uri): Boolean =
        runOrNull {
            val projection = arrayOf(MediaStore.Images.Media._ID)
            contentResolver
                .query(uri, projection, null, null, null)
                ?.use { it.moveToFirst() } ?: false
        } ?: false

    private fun readNameAndSize(uri: Uri): Pair<String?, Long> {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
        return runOrNull {
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
                    val size = if (sizeIndex >= 0) cursor.getLong(sizeIndex) else 0L
                    name to size
                } else {
                    null
                }
            }
        } ?: return null to 0L
    }
}
