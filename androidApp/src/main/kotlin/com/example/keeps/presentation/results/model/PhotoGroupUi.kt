package com.example.keeps.presentation.results.model

/**
 * A group of visually-similar mock photos.
 */
data class PhotoGroupUi(
    val id: String,
    val similarityPercent: Int,
    val photos: List<PhotoUi>,
)
