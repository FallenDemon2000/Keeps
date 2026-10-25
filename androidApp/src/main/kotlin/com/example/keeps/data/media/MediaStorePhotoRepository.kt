package com.example.keeps.data.media

import android.content.IntentSender
import android.net.Uri
import com.example.keeps.domain.model.Photo
import com.example.keeps.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Loads photo metadata and builds delete requests through device media access. */
class MediaStorePhotoRepository(
    private val photoDataSource: PhotoDataSource,
) : PhotoRepository {

    override suspend fun loadPhotos(uris: List<Uri>): List<Photo> =
        withContext(Dispatchers.IO) {
            uris.map { photoDataSource.readPhoto(it) }
        }

    override fun createDeleteRequest(uris: List<Uri>): IntentSender =
        photoDataSource.createDeleteRequest(uris)
}
