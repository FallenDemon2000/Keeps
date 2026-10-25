package com.example.keeps.domain.repository

import com.example.keeps.domain.model.Photo

/** Provides photo fingerprints, reusing stored hashes when available. */
interface PhotoHashRepository {
    /** Returns null when no hash can be computed for the photo. */
    fun hash(photo: Photo): Long?
}
