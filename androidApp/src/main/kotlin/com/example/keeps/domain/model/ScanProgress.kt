package com.example.keeps.domain.model

import kotlin.math.ceil

/** How far the running (or last) scan has got. */
data class ScanProgress(
    val analyzedPhotoCount: Int = 0,
    val totalPhotoCount: Int = 0,
    val isComplete: Boolean = true,
) {
    val fraction: Float
        get() = if (totalPhotoCount == 0) 1f else analyzedPhotoCount.toFloat() / totalPhotoCount

    /** Photos that must be analyzed before the first (partial) results are shown. */
    val photosNeededForFirstResults: Int
        get() = maxOf(
            ceil(totalPhotoCount * FIRST_RESULTS_FRACTION).toInt(),
            minOf(totalPhotoCount, FIRST_RESULTS_MIN_PHOTOS),
        )

    val isReadyForFirstResults: Boolean
        get() = isComplete || analyzedPhotoCount >= photosNeededForFirstResults

    /** Progress of the loading screen, which fills up at the first-results gate. */
    val loadingFraction: Float
        get() = if (isReadyForFirstResults) {
            1f
        } else {
            analyzedPhotoCount.toFloat() / photosNeededForFirstResults
        }

    companion object {
        const val FIRST_RESULTS_FRACTION = 0.25
        const val FIRST_RESULTS_MIN_PHOTOS = 8
    }
}
