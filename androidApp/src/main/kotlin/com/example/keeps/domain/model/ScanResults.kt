package com.example.keeps.domain.model

/** Groups and the configured threshold captured when their scan started. */
data class ScanResults(
    val groups: List<PhotoGroup>,
    val similarityThreshold: Float,
) {
    companion object {
        val EMPTY
            get() = ScanResults(
                groups = emptyList(),
                similarityThreshold = 0f,
            )
    }
}
