package com.example.keeps.domain.repository

import android.content.IntentSender
import android.net.Uri
import com.example.keeps.domain.model.Photo

/** Provides metadata and system deletion requests for selected photos. */
interface PhotoRepository {
    suspend fun loadPhotos(uris: List<Uri>): List<Photo>

    /** Builds the system confirmation dialog that deletes [uris] from device storage. */
    fun createDeleteRequest(uris: List<Uri>): IntentSender
}
