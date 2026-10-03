package com.example.keeps.presentation

import androidx.compose.ui.graphics.Brush
import com.example.keeps.data.media.Photo
import com.example.keeps.domain.model.PhotoGroup
import com.example.keeps.presentation.results.model.PhotoGroupUi
import com.example.keeps.presentation.results.model.PhotoUi

/**
 * Maps domain [PhotoGroup]/[Photo] (real, picked photos) to the Results
 * screen's presentation models. The gradient [Brush] is kept only as a
 * loading/fallback background behind the real Coil thumbnail (see
 * `SelectableMediaTile`); the first photo in each group is marked as the
 * "keep" candidate, matching the existing mock dataset's convention.
 */
fun List<PhotoGroup>.toUiGroups(): List<PhotoGroupUi> = map { group ->
    PhotoGroupUi(
        id = group.id,
        similarityPercent = group.similarityPercent,
        photos = group.photos.map { it.toUiPhoto() },
    )
}

private fun Photo.toUiPhoto(): PhotoUi {
    val megabytes = sizeBytes / (1024.0 * 1024.0)
    val sizeText = "%.1f MB".format(megabytes)

    return PhotoUi(
        id = id,
        imageUri = uri,
        sizeBytes = sizeBytes,
        sizeText = sizeText,
        dimensionsText = "$width\u00D7$height",
    )
}
