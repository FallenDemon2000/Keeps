package com.example.keeps.domain.usecase

import android.net.Uri
import com.example.keeps.domain.model.GroupingUpdate
import kotlinx.coroutines.flow.Flow

/** Emits partial groups as photos are analyzed, ending with coverage of every photo. */
fun interface GroupPhotosUseCase {
    suspend operator fun invoke(uris: List<Uri>, similarityThreshold: Float): Flow<GroupingUpdate>
}
