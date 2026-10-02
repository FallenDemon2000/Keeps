package com.example.keeps.presentation.results.model

import androidx.compose.ui.graphics.Brush

/**
 * A single mock photo within a duplicate group.
 */
data class PhotoUi(
    val id: String,
    val placeholder: Brush,
    val sizeBytes: Long,
    val sizeText: String,
    val dimensionsText: String,
)
