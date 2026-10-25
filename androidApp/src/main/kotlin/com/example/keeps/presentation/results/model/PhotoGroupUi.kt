package com.example.keeps.presentation.results.model

/**
 * A group of photos shown as one card. [isUnique] marks the special bucket of
 * photos that matched nothing else; it has no similarity score.
 */
data class PhotoGroupUi(
    val id: String,
    val similarityPercent: Int,
    val photos: List<PhotoUi>,
    val isUnique: Boolean = false,
)
