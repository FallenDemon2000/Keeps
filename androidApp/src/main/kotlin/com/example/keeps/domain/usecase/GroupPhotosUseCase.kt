package com.example.keeps.domain.usecase

import android.net.Uri
import com.example.keeps.data.media.PhotoRepository
import com.example.keeps.domain.model.PhotoGroup
import com.example.keeps.domain.usecase.RandomPhotoGroupingUseCase.Companion.DEFAULT_GROUP_SIZE
import kotlin.random.Random

/** Hard cap on how many photos a single scan can process. */
const val MAX_SCAN_PHOTOS = 10

/**
 * Groups scanned photos into "duplicate" sets. Grouping is the one piece of
 * this feature intentionally left unimplemented for now — see
 * [RandomPhotoGroupingUseCase].
 */
fun interface GroupPhotosUseCase {
    suspend operator fun invoke(uris: List<Uri>): List<PhotoGroup>
}

/**
 * Placeholder grouping: **not** real similarity/perceptual-hash analysis.
 * Photos are simply chunked, in selection order, into fixed-size groups of
 * [DEFAULT_GROUP_SIZE] (the final group may be smaller), each assigned a random
 * similarity score and a random "keep" candidate. This exists purely so the
 * Results screen has real (if arbitrary) groups to render; replace this with
 * a true similarity-clustering implementation once that's available.
 */
class RandomPhotoGroupingUseCase(
    private val photoRepository: PhotoRepository,
) : GroupPhotosUseCase {

    private val random = Random.Default

    override suspend fun invoke(uris: List<Uri>): List<PhotoGroup> {
        val photos = photoRepository.loadPhotos(uris).take(MAX_SCAN_PHOTOS)
        return photos
            .take(MAX_SCAN_PHOTOS)
            .chunked(DEFAULT_GROUP_SIZE)
            .mapIndexed { index, groupPhotos ->
                PhotoGroup(
                    id = "group-$index",
                    similarityPercent = random.nextInt(MIN_SIMILARITY, MAX_SIMILARITY + 1),
                    photos = groupPhotos,
                )
            }
    }

    private companion object {
        const val DEFAULT_GROUP_SIZE = 2
        const val MIN_SIMILARITY = 70
        const val MAX_SIMILARITY = 99
    }
}
