package com.example.keeps.domain.model

import com.example.keeps.data.media.Photo

/**
 * A group of photos considered "duplicates" of one another. Today, grouping is
 * produced by a hardcoded/random placeholder (see `GroupPhotosUseCase`), so
 * [similarityPercent] is not yet a real perceptual-similarity score.
 */
data class PhotoGroup(
    val id: String,
    val similarityPercent: Int,
    val photos: List<Photo>,
)
