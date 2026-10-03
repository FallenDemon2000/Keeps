package com.example.keeps.presentation.results.model

import android.net.Uri

/**
 * A single photo within a duplicate group, as rendered by the Results screen.
 * [imageUri] is set for real, picked photos (rendered via Coil); it's null for
 * the static preview/mock dataset in `FakeResultsData`, which falls back to
 * placeholder.
 */
data class PhotoUi(
    val id: String,
    val imageUri: Uri,
    val sizeBytes: Long,
    val sizeText: String,
    val dimensionsText: String,
)
