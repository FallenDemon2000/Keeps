package com.example.keeps.domain.scan

import kotlin.math.round

/**
 * Difference hash (dHash): compares horizontally adjacent pixels of a tiny
 * grayscale thumbnail, producing a 64-bit fingerprint that is stable under
 * resizing, compression and small color shifts.
 */
object DHasher {
    const val HASH_WIDTH = 9
    const val HASH_HEIGHT = 8
    const val PIXEL_COUNT = HASH_WIDTH * HASH_HEIGHT
    const val HASH_BITS = 64

    /**
     * @param grayscale row-major luminance values (0..255) of a
     * [HASH_WIDTH] x [HASH_HEIGHT] thumbnail.
     */
    fun hash(grayscale: IntArray): Long {
        require(grayscale.size == PIXEL_COUNT) {
            "Expected $PIXEL_COUNT pixels but got ${grayscale.size}"
        }
        var hash = 0L
        for (row in 0 until HASH_HEIGHT) {
            for (col in 0 until HASH_WIDTH - 1) {
                val index = row * HASH_WIDTH + col
                hash = hash shl 1
                if (grayscale[index] > grayscale[index + 1]) hash = hash or 1L
            }
        }
        return hash
    }

    /**
     * Box-averages a row-major grayscale image of any size down to the
     * [HASH_WIDTH] x [HASH_HEIGHT] grid and hashes it. Decoders should hand over a
     * thumbnail already close to that size to keep this cheap.
     */
    fun hashFromImage(grayscale: IntArray, width: Int, height: Int): Long {
        require(width >= HASH_WIDTH && height >= HASH_HEIGHT && grayscale.size == width * height) {
            "Image must be at least ${HASH_WIDTH}x$HASH_HEIGHT with matching pixel count"
        }
        val cells = IntArray(PIXEL_COUNT)
        for (cellRow in 0 until HASH_HEIGHT) {
            val yStart = cellRow * height / HASH_HEIGHT
            val yEnd = (cellRow + 1) * height / HASH_HEIGHT
            for (cellCol in 0 until HASH_WIDTH) {
                val xStart = cellCol * width / HASH_WIDTH
                val xEnd = (cellCol + 1) * width / HASH_WIDTH
                var sum = 0L
                for (y in yStart until yEnd) {
                    for (x in xStart until xEnd) sum += grayscale[y * width + x]
                }
                cells[cellRow * HASH_WIDTH + cellCol] =
                    (sum / ((yEnd - yStart) * (xEnd - xStart))).toInt()
            }
        }
        return hash(cells)
    }

    fun hammingDistance(a: Long, b: Long): Int = (a xor b).countOneBits()

    /** Maps a similarity ratio (0..1) to the largest Hamming distance still considered similar. */
    fun maxDistanceFor(similarity: Float): Int =
        ((1f - similarity.coerceIn(0f, 1f)) * HASH_BITS).let { round(it).toInt() }

    fun similarityPercent(averageDistance: Double): Int =
        round((1.0 - averageDistance / HASH_BITS) * PERCENT).toInt().coerceIn(0, PERCENT)

    private const val PERCENT = 100
}
