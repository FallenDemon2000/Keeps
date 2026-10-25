package com.example.keeps.domain.usecase

import android.net.Uri
import com.example.keeps.domain.model.GroupKind
import com.example.keeps.domain.model.GroupingUpdate
import com.example.keeps.domain.model.Photo
import com.example.keeps.domain.model.PhotoGroup
import com.example.keeps.domain.repository.PhotoHashRepository
import com.example.keeps.domain.repository.PhotoRepository
import com.example.keeps.domain.scan.DHasher
import com.example.keeps.domain.scan.PhotoGrouper
import com.example.keeps.domain.scan.model.HashedPhoto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/** ID of the bucket that collects every photo with no similar match. */
const val UNIQUE_GROUP_ID = "unique"

/**
 * Hashes photos with bounded parallelism and periodically regroups analyzed photos
 * using Hamming distance. Later photos may join or merge earlier groups.
 */
class SimilarityGroupPhotosUseCase(
    private val photoRepository: PhotoRepository,
    private val photoHashRepository: PhotoHashRepository,
) : GroupPhotosUseCase {

    private val parallelism
        get() = Runtime.getRuntime().availableProcessors()
            .coerceIn(MIN_PARALLELISM, MAX_PARALLELISM)

    override suspend fun invoke(uris: List<Uri>, similarityThreshold: Float): Flow<GroupingUpdate> =
        channelFlow {
            val photos = photoRepository.loadPhotos(uris)
            val maxHammingDistance = DHasher.maxDistanceFor(similarityThreshold)
            val hashResults = Channel<HashedPhoto>(Channel.UNLIMITED)

            launchHashWorkers(photos, hashResults)
            publishGroupingUpdates(photos, hashResults, maxHammingDistance) { send(it) }
            hashResults.close()
        }

    private fun CoroutineScope.launchHashWorkers(
        photos: List<Photo>,
        hashResults: SendChannel<HashedPhoto>,
    ) {
        val semaphore = Semaphore(parallelism)
        photos.forEach { photo ->
            launch(Dispatchers.Default) {
                semaphore.withPermit {
                    hashResults.send(HashedPhoto(photo.id, photoHashRepository.hash(photo)))
                }
            }
        }
    }

    private suspend fun publishGroupingUpdates(
        photos: List<Photo>,
        hashResults: ReceiveChannel<HashedPhoto>,
        maxHammingDistance: Int,
        publish: suspend (GroupingUpdate) -> Unit,
    ) {
        val totalPhotoCount = photos.size
        // Missing key means pending analysis; a present null means decoding failed.
        val analyzedHashes = HashMap<String, Long?>(totalPhotoCount)
        val batchSize = maxOf(1, totalPhotoCount / TARGET_GROUPING_UPDATES)
        publish(GroupingUpdate(0, totalPhotoCount, emptyList()))

        repeat(totalPhotoCount) {
            val hashedPhoto = hashResults.receive()
            analyzedHashes[hashedPhoto.id] = hashedPhoto.hash
            if (analyzedHashes.size % batchSize == 0 || analyzedHashes.size == totalPhotoCount) {
                publish(
                    GroupingUpdate(
                        analyzedPhotoCount = analyzedHashes.size,
                        totalPhotoCount = totalPhotoCount,
                        groups = buildGroups(photos, analyzedHashes, maxHammingDistance),
                    ),
                )
            }
        }
    }

    private fun buildGroups(
        photos: List<Photo>,
        analyzedHashes: Map<String, Long?>,
        maxHammingDistance: Int,
    ): List<PhotoGroup> {
        val analyzedPhotos = photos.filter { it.id in analyzedHashes }
        val photosById = analyzedPhotos.associateBy { it.id }
        val result = PhotoGrouper.group(
            analyzedPhotos.map { HashedPhoto(it.id, analyzedHashes[it.id]) },
            maxHammingDistance,
        )

        val similarGroups = result.groups.map { group ->
            PhotoGroup(
                id = group.id,
                similarityPercent = group.similarityPercent,
                photos = group.memberIds.map { photosById.getValue(it) },
            )
        }
        val uniqueGroup = result.uniqueIds
            .takeIf { it.isNotEmpty() }
            ?.let { ids ->
                PhotoGroup(
                    id = UNIQUE_GROUP_ID,
                    similarityPercent = 0,
                    photos = ids.map { photosById.getValue(it) },
                    kind = GroupKind.Unique,
                )
            }
        return similarGroups + listOfNotNull(uniqueGroup)
    }

    private companion object {
        /** Approximate batching target, not a guaranteed number of emitted updates. */
        const val TARGET_GROUPING_UPDATES = 20
        const val MIN_PARALLELISM = 2
        const val MAX_PARALLELISM = 6
    }
}
