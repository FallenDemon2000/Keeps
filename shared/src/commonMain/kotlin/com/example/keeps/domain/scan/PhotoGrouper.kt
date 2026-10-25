package com.example.keeps.domain.scan

import com.example.keeps.domain.scan.model.GroupingResult
import com.example.keeps.domain.scan.model.HashedPhoto
import com.example.keeps.domain.scan.model.SimilarGroup

/**
 * Groups photos whose hashes are within max distance bits of each other using
 * union-find, so every photo ends up in exactly one group. Grouping is
 * transitive: if A~B and B~C, then A, B and C share a group.
 */
object PhotoGrouper {

    fun group(photos: List<HashedPhoto>, maxHammingDistance: Int): GroupingResult {
        val hashed = photos.filter { it.hash != null }
        val parents = IntArray(hashed.size) { it }
        parents.mergeClusters(hashed, maxHammingDistance)

        val clusters = hashed.indices.groupBy { parents.find(it) }.values
        val groups = mutableListOf<SimilarGroup>()
        val uniqueIds = mutableListOf<String>()
        for (cluster in clusters) {
            if (cluster.size < 2) {
                uniqueIds += hashed[cluster.first()].id
            } else {
                groups += buildGroup(cluster.map { hashed[it] })
            }
        }
        photos.filter { it.hash == null }.forEach { uniqueIds += it.id }

        val order = photos.withIndex().associate { it.value.id to it.index }
        return GroupingResult(
            groups = groups.sortedBy { group -> group.memberIds.minOf { order.getValue(it) } },
            uniqueIds = uniqueIds.sortedBy { order.getValue(it) },
        )
    }

    private fun IntArray.mergeClusters(hashed: List<HashedPhoto>, maxHammingDistance: Int) {
        for (i in hashed.indices) {
            val hashI = hashed[i].hash!!
            for (j in i + 1 until hashed.size) {
                if (DHasher.hammingDistance(hashI, hashed[j].hash!!) <= maxHammingDistance) {
                    val rootI = find(i)
                    val rootJ = find(j)
                    if (rootI != rootJ) this[rootJ] = rootI
                }
            }
        }
    }

    private fun IntArray.find(x: Int): Int {
        var root = x
        while (this[root] != root) root = this[root]
        var node = x
        while (this[node] != root) {
            val next = this[node]
            this[node] = root
            node = next
        }
        return root
    }

    private fun buildGroup(members: List<HashedPhoto>): SimilarGroup {
        var total = 0L
        var pairs = 0
        for (i in members.indices) {
            for (j in i + 1 until members.size) {
                total += DHasher.hammingDistance(members[i].hash!!, members[j].hash!!)
                pairs++
            }
        }

        return SimilarGroup(
            id = members.minOf { it.id },
            memberIds = members.map { it.id },
            similarityPercent = DHasher.similarityPercent(total.toDouble() / pairs),
        )
    }
}
