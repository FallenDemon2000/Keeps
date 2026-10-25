package com.example.keeps.domain.scan.model

/** A photo identified by [id] and its dHash, or null when it could not be decoded. */
data class HashedPhoto(
    val id: String,
    val hash: Long?,
)
