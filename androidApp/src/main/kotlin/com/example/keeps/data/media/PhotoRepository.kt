package com.example.keeps.data.media

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Turns photo URIs returned by the system photo picker into domain [Photo]
 * objects, reading their metadata via [PhotoDataSource].
 */
interface PhotoRepository {
    suspend fun loadPhotos(uris: List<Uri>): List<Photo>
}

class MediaStorePhotoRepository(
    private val photoDataSource: PhotoDataSource,
) : PhotoRepository {

    override suspend fun loadPhotos(uris: List<Uri>): List<Photo> =
        withContext(Dispatchers.IO) {
            uris.map { photoDataSource.readPhoto(it) }
        }
}
