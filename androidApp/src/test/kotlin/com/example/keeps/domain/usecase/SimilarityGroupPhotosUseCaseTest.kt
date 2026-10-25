package com.example.keeps.domain.usecase

import android.content.IntentSender
import android.net.Uri
import com.example.keeps.domain.model.GroupKind
import com.example.keeps.domain.model.Photo
import com.example.keeps.domain.repository.PhotoHashRepository
import com.example.keeps.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimilarityGroupPhotosUseCaseTest {

    @Test
    fun progressiveGroupsCoverExactlyTheAnalyzedPhotos() = runTest {
        val photos = (0 until 40).map { photo("photo-$it") }
        val hashes = photos.associate { it.id to if (it.id == "photo-0") null else 0L }
        val useCase = SimilarityGroupPhotosUseCase(
            FakePhotoRepository(photos),
            object : PhotoHashRepository {
                override fun hash(photo: Photo): Long? = hashes.getValue(photo.id)
            },
        )

        val updates = useCase(emptyList(), 0.8f).toList()

        assertEquals(0, updates.first().analyzedPhotoCount)
        assertEquals(40, updates.last().analyzedPhotoCount)
        assertEquals(
            updates.map { it.analyzedPhotoCount }.sorted(),
            updates.map { it.analyzedPhotoCount },
        )
        assertTrue(updates.any { it.analyzedPhotoCount in 1 until 40 })
        updates.forEach { update ->
            val ids = update.groups.flatMap { group -> group.photos.map { it.id } }
            assertEquals(40, update.totalPhotoCount)
            assertEquals(update.analyzedPhotoCount, ids.size)
            assertEquals(ids.size, ids.toSet().size)
        }
        val finalGroups = updates.last().groups
        assertEquals(
            photos.map { it.id }.toSet(),
            finalGroups.flatMap { group -> group.photos.map { it.id } }.toSet(),
        )
        assertEquals(
            listOf("photo-0"),
            finalGroups.single { it.kind == GroupKind.Unique }.photos.map { it.id },
        )
    }

    @Test
    fun emptySelectionEmitsAnEmptyUpdate() = runTest {
        val useCase = SimilarityGroupPhotosUseCase(
            FakePhotoRepository(emptyList()),
            object : PhotoHashRepository {
                override fun hash(photo: Photo): Long = error("No photo should be hashed")
            },
        )

        val updates = useCase(emptyList(), 0.8f).toList()

        assertEquals(1, updates.size)
        assertEquals(0, updates.single().totalPhotoCount)
        assertTrue(updates.single().groups.isEmpty())
    }

    private fun photo(id: String) = Photo(id, Uri.EMPTY, 10L, 100, 100, "image/jpeg")

    private class FakePhotoRepository(private val photos: List<Photo>) : PhotoRepository {
        override suspend fun loadPhotos(uris: List<Uri>): List<Photo> = photos

        override fun createDeleteRequest(uris: List<Uri>): IntentSender = error("Not used")
    }
}
