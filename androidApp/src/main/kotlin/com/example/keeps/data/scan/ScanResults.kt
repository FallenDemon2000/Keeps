package com.example.keeps.data.scan

import com.example.keeps.domain.model.PhotoGroup

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
