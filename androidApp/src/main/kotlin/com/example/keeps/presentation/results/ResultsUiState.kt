package com.example.keeps.presentation.results

import com.example.keeps.presentation.results.model.PhotoGroupUi

/**
 * Renderable state for the Results screen.
 */
data class ResultsUiState(
    val groups: List<PhotoGroupUi> = emptyList(),
    val selectedPhotoIds: Map<String, Set<String>> = emptyMap(),
) {
    val totalPhotoCount: Int get() = groups.sumOf { it.photos.size }
    val selectedCount: Int get() = selectedPhotoIds.values.sumOf { it.size }
    val isEmpty: Boolean get() = groups.isEmpty()
    val selectedBytes: Long
        get() = groups.asSequence()
            .flatMap { it.photos.asSequence() }
            .filter { it.id in selectedPhotoIds.values.flatten() }
            .sumOf { it.sizeBytes }
}
