package com.example.keeps.domain.scan.model

data class GroupingResult(
    val groups: List<SimilarGroup>,
    /** Photos that matched nothing (including photos that failed to hash). */
    val uniqueIds: List<String>,
)
