package com.example.keeps.domain.scan

/** Persists computed dHashes so repeat scans can skip decoding. */
interface PhotoHashDataSource {
    /** Returns null when missing or when the photo's file size has changed. */
    fun get(photoId: String, sizeBytes: Long): Long?

    /** Persists a photo's dHash, overwriting any existing value. */
    fun put(photoId: String, sizeBytes: Long, hash: Long)
}
