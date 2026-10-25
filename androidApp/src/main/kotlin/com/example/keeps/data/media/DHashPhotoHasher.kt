package com.example.keeps.data.media

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import com.example.keeps.domain.model.Photo
import com.example.keeps.domain.scan.DHasher
import com.example.keeps.domain.scan.PhotoHasher
import com.example.keeps.ktx.runOrNull

/**
 * Decodes each photo straight to a tiny thumbnail (the decoder downsamples
 * while reading, so full-size bitmaps are never held in memory), converts it to
 * grayscale and hashes it with [DHasher]. Caching belongs to
 * [PhotoHashRepositoryImpl].
 */
class DHashPhotoHasher(
    private val contentResolver: ContentResolver,
) : PhotoHasher {

    override fun hash(photo: Photo): Long? = runOrNull { decodeAndHash(photo) }

    private fun decodeAndHash(photo: Photo): Long {
        val source = ImageDecoder.createSource(contentResolver, photo.uri)
        val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.setTargetSize(THUMB_WIDTH, THUMB_HEIGHT)
        }
        return try {
            DHasher.hashFromImage(bitmap.toGrayscale(), bitmap.width, bitmap.height)
        } finally {
            bitmap.recycle()
        }
    }

    private fun Bitmap.toGrayscale(): IntArray {
        val argb = IntArray(width * height)
        getPixels(argb, 0, width, 0, 0, width, height)
        return IntArray(argb.size) { i ->
            val pixel = argb[i]
            val red = pixel shr RED_SHIFT and CHANNEL_MASK
            val green = pixel shr GREEN_SHIFT and CHANNEL_MASK
            val blue = pixel and CHANNEL_MASK
            (LUMA_RED * red + LUMA_GREEN * green + LUMA_BLUE * blue) / LUMA_SCALE
        }
    }

    private companion object {
        const val THUMB_WIDTH = DHasher.HASH_WIDTH * 8
        const val THUMB_HEIGHT = DHasher.HASH_HEIGHT * 8
        const val RED_SHIFT = 16
        const val GREEN_SHIFT = 8
        const val CHANNEL_MASK = 0xFF
        const val LUMA_RED = 299
        const val LUMA_GREEN = 587
        const val LUMA_BLUE = 114
        const val LUMA_SCALE = 1000
    }
}
