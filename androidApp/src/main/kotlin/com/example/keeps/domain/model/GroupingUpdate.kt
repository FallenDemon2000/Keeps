package com.example.keeps.domain.model

/** Groups covering [analyzedPhotoCount] of [totalPhotoCount] selected photos. */
data class GroupingUpdate(
    val analyzedPhotoCount: Int,
    val totalPhotoCount: Int,
    val groups: List<PhotoGroup>,
)
