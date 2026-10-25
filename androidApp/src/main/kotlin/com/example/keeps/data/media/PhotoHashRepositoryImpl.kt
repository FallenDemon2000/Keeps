package com.example.keeps.data.media

import com.example.keeps.domain.model.Photo
import com.example.keeps.domain.repository.PhotoHashRepository
import com.example.keeps.domain.scan.PhotoHashDataSource
import com.example.keeps.domain.scan.PhotoHasher
import com.example.keeps.ktx.runOrNull

/** Coordinates cached hashes and image hashing, avoiding repeat decoding. */
class PhotoHashRepositoryImpl(
    private val photoHashDataSource: PhotoHashDataSource,
    private val photoHasher: PhotoHasher,
) : PhotoHashRepository {

    override fun hash(photo: Photo): Long? {
        val cachedHash = runOrNull { photoHashDataSource.get(photo.id, photo.sizeBytes) }
        if (cachedHash != null) return cachedHash

        val hash = photoHasher.hash(photo) ?: return null
        runOrNull { photoHashDataSource.put(photo.id, photo.sizeBytes, hash) }
        return hash
    }
}
