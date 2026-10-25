package com.example.keeps.domain.usecase

import android.content.IntentSender
import android.net.Uri
import com.example.keeps.domain.repository.PhotoRepository

/**
 * Builds the system confirmation request that permanently deletes the given
 * photos from device storage once the user approves it.
 */
class CreateDeleteRequestUseCase(
    private val photoRepository: PhotoRepository,
) {
    operator fun invoke(uris: List<Uri>): IntentSender =
        photoRepository.createDeleteRequest(uris)
}
