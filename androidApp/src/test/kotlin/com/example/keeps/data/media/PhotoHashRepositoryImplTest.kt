package com.example.keeps.data.media

import android.net.Uri
import com.example.keeps.domain.model.Photo
import com.example.keeps.domain.scan.PhotoHashDataSource
import com.example.keeps.domain.scan.PhotoHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PhotoHashRepositoryImplTest {

    private val photo = Photo("photo", Uri.EMPTY, 10L, 100, 100, "image/jpeg")
    private val dataSource = FakePhotoHashDataSource()
    private var hashCalls = 0

    private fun repository(computedHash: Long?) = PhotoHashRepositoryImpl(
        dataSource,
        PhotoHasher {
            hashCalls++
            computedHash
        },
    )

    @Test
    fun cachedHashSkipsDecoding() {
        dataSource.put(photo.id, photo.sizeBytes, 0L)

        assertEquals(0L, repository(42L).hash(photo))
        assertEquals(0, hashCalls)
    }

    @Test
    fun missingHashIsComputedAndStored() {
        val repository = repository(-1L)

        assertEquals(-1L, repository.hash(photo))
        assertEquals(-1L, repository.hash(photo))
        assertEquals(-1L, dataSource.get(photo.id, photo.sizeBytes))
        assertEquals(1, hashCalls)
    }

    @Test
    fun changedFileSizeRecomputesHash() {
        dataSource.put(photo.id, photo.sizeBytes - 1, 0L)

        assertEquals(42L, repository(42L).hash(photo))
        assertEquals(42L, dataSource.get(photo.id, photo.sizeBytes))
        assertEquals(1, hashCalls)
    }

    @Test
    fun undecodablePhotoIsNotCached() {
        assertNull(repository(null).hash(photo))
        assertNull(dataSource.get(photo.id, photo.sizeBytes))
        assertEquals(0, dataSource.writes)
    }

    private class FakePhotoHashDataSource : PhotoHashDataSource {
        private val hashes = mutableMapOf<String, Pair<Long, Long>>()
        var writes = 0
            private set

        override fun get(photoId: String, sizeBytes: Long): Long? =
            hashes[photoId]?.takeIf { it.first == sizeBytes }?.second

        override fun put(photoId: String, sizeBytes: Long, hash: Long) {
            hashes[photoId] = sizeBytes to hash
            writes++
        }
    }
}
