package com.example.keeps.domain.scan

import com.example.keeps.domain.model.Photo

/** Computes a photo's fingerprint without consulting persistent storage. */
fun interface PhotoHasher {
    /** Returns null when the photo cannot be decoded. */
    fun hash(photo: Photo): Long?
}
