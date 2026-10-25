package com.example.keeps.presentation.results.model

/**
 * Renderable state for the Results screen. While [isAnalyzing] is true the scan
 * is still running in the background: [groups] are partial and may still change,
 * and deleting is disabled.
 */
data class ResultsUiState(
    val groups: List<PhotoGroupUi> = emptyList(),
    val selectedPhotoIds: Map<String, Set<String>> = emptyMap(),
    val similarityThreshold: Int = 80,
    val isAnalyzing: Boolean = false,
    val analyzedFraction: Float = 1f,
) {
    val totalPhotoCount: Int get() = groups.sumOf { it.photos.size }
    val similarGroupCount: Int get() = groups.count { !it.isUnique }
    val selectedCount: Int get() = selectedPhotoIds.values.sumOf { it.size }
    val isEmpty: Boolean get() = groups.isEmpty()
    val selectedBytes: Long
        get() = groups.asSequence()
            .flatMap { it.photos.asSequence() }
            .filter { it.id in selectedPhotoIds.values.flatten() }
            .sumOf { it.sizeBytes }
}
