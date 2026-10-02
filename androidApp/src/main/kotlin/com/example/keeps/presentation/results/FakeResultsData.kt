package com.example.keeps.presentation.results

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.keeps.presentation.results.model.PhotoGroupUi
import com.example.keeps.presentation.results.model.PhotoUi

/**
 * Static sample dataset standing in for the (not-yet-built) real dedup engine. Every
 * value here is fake: gradient placeholders instead of real photo thumbnails, and
 * made-up file sizes/dimensions matching `specs/keeps-design.html`.
 */
object FakeResultsData {

    private val placeholder1 = gradient(0xFF1A3A4A, 0xFF2D5A70, 0xFF1A4A3A)
    private val placeholder2 = gradient(0xFF3A2D1A, 0xFF6B4A2D, 0xFF4A3A2D)

    val sampleGroups: List<PhotoGroupUi> = listOf(
        PhotoGroupUi(
            id = "group-1",
            similarityPercent = 98,
            photos = listOf(
                PhotoUi(
                    id = "photo-1a",
                    placeholder = placeholder1,
                    sizeBytes = 4_200_000,
                    sizeText = "4.2 MB",
                    dimensionsText = "4032\u00D73024",
                ),
                PhotoUi(
                    id = "photo-1b",
                    placeholder = placeholder1,
                    sizeBytes = 3_800_000,
                    sizeText = "3.8 MB",
                    dimensionsText = "3840\u00D72880",
                ),
                PhotoUi(
                    id = "photo-1c",
                    placeholder = placeholder1,
                    sizeBytes = 1_200_000,
                    sizeText = "1.2 MB",
                    dimensionsText = "1920\u00D71440",
                ),
                PhotoUi(
                    id = "photo-1d",
                    placeholder = placeholder1,
                    sizeBytes = 3_900_000,
                    sizeText = "3.9 MB",
                    dimensionsText = "4032\u00D73024",
                ),
            ),
        ),
        PhotoGroupUi(
            id = "group-2",
            similarityPercent = 83,
            photos = listOf(
                PhotoUi(
                    id = "photo-2a",
                    placeholder = placeholder2,
                    sizeBytes = 2_100_000,
                    sizeText = "2.1 MB",
                    dimensionsText = "3024\u00D72016",
                ),
                PhotoUi(
                    id = "photo-2b",
                    placeholder = placeholder2,
                    sizeBytes = 1_900_000,
                    sizeText = "1.9 MB",
                    dimensionsText = "2880\u00D71920",
                ),
            ),
        ),
        PhotoGroupUi(
            id = "group-3",
            similarityPercent = 76,
            photos = listOf(
                PhotoUi(
                    id = "photo-3a",
                    placeholder = gradient(0xFF2A1A3A, 0xFF4A2D6B, 0xFF3A2A4A),
                    sizeBytes = 5_100_000,
                    sizeText = "5.1 MB",
                    dimensionsText = "4096\u00D73072",
                ),
                PhotoUi(
                    id = "photo-3b",
                    placeholder = gradient(0xFF2A1A3A, 0xFF4A2D6B, 0xFF3A2A4A),
                    sizeBytes = 2_300_000,
                    sizeText = "2.3 MB",
                    dimensionsText = "2048\u00D71536",
                ),
            ),
        ),
    )

    private fun gradient(vararg colors: Long) = Brush.linearGradient(colors.map { Color(it) })
}
