package com.example.keeps.domain.scan

import com.example.keeps.domain.scan.model.HashedPhoto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhotoGrouperTest {

    @Test
    fun transitiveMatchesShareOneGroupAndUnmatchedPhotosStayUnique() {
        val photos = listOf(
            HashedPhoto("a", 0L),
            HashedPhoto("b", 1L),
            HashedPhoto("c", 3L),
            HashedPhoto("different", -1L),
            HashedPhoto("undecodable", null),
        )

        val result = PhotoGrouper.group(photos, maxHammingDistance = 1)

        assertEquals(listOf("a", "b", "c"), result.groups.single().memberIds)
        assertEquals(listOf("different", "undecodable"), result.uniqueIds)
        val allIds = result.groups.flatMap { it.memberIds } + result.uniqueIds
        assertEquals(photos.map { it.id }.sorted(), allIds.sorted())
    }

    @Test
    fun emptyInputProducesNoGroups() {
        val result = PhotoGrouper.group(emptyList(), maxHammingDistance = 0)

        assertTrue(result.groups.isEmpty())
        assertTrue(result.uniqueIds.isEmpty())
    }
}
