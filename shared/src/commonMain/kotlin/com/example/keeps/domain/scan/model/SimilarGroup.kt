package com.example.keeps.domain.scan.model

/**
 * Two or more similar photos. [id] is the smallest member id: deterministic for
 * the same membership, but it may change when a smaller-id member joins or groups merge.
 * [similarityPercent] is the average pairwise hash similarity, not a guaranteed
 * minimum similarity between every pair or a probability of duplication.
 */
data class SimilarGroup(
    val id: String,
    val memberIds: List<String>,
    val similarityPercent: Int,
)
