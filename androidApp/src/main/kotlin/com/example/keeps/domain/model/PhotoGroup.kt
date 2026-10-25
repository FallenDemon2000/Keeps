package com.example.keeps.domain.model

enum class GroupKind {
    /** Photos whose perceptual hashes are close to each other. */
    Similar,

    /** The single bucket holding every photo that matched nothing else. */
    Unique,
}

/**
 * A group of photos. For [GroupKind.Similar] groups, [similarityPercent] is the
 * average perceptual similarity between the members; the [GroupKind.Unique]
 * bucket has no meaningful similarity.
 */
data class PhotoGroup(
    val id: String,
    val similarityPercent: Int,
    val photos: List<Photo>,
    val kind: GroupKind = GroupKind.Similar,
)
