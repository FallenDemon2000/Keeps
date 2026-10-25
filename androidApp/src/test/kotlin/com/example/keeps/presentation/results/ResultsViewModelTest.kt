package com.example.keeps.presentation.results

import android.content.IntentSender
import android.net.Uri
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.domain.model.Photo
import com.example.keeps.domain.model.PhotoGroup
import com.example.keeps.domain.repository.PhotoRepository
import com.example.keeps.domain.usecase.CreateDeleteRequestUseCase
import com.example.keeps.presentation.results.model.ResultsAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResultsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectedPhotosFollowMergedGroupsWhileDeletionStaysBlocked() = runTest(dispatcher) {
        val repository = ScanResultsRepository()
        repository.initializeScan(4, 0.8f)
        val photos = listOf(photo("a"), photo("b"), photo("c"), photo("d"))
        repository.updateScan(
            4,
            listOf(
                PhotoGroup("a", 100, photos.take(2)),
                PhotoGroup("c", 100, photos.drop(2)),
            ),
        )
        var deleteRequestCount = 0
        val photoRepository = object : PhotoRepository {
            override suspend fun loadPhotos(uris: List<Uri>): List<Photo> = error("Not used")

            override fun createDeleteRequest(uris: List<Uri>): IntentSender {
                deleteRequestCount++
                error("Deletion must stay blocked during analysis")
            }
        }
        val viewModel = ResultsViewModel(repository, CreateDeleteRequestUseCase(photoRepository))
        runCurrent()

        viewModel.onAction(ResultsAction.TogglePhotoSelected("c", "d"))
        repository.updateScan(4, listOf(PhotoGroup("a", 95, photos)))
        runCurrent()

        assertEquals(mapOf("a" to setOf("d")), viewModel.resultsState.value.selectedPhotoIds)
        viewModel.onAction(ResultsAction.DeleteSelected)
        viewModel.onAction(ResultsAction.KeepSelected)
        assertEquals(0, deleteRequestCount)
        assertEquals(mapOf("a" to setOf("d")), viewModel.resultsState.value.selectedPhotoIds)
    }

    private fun photo(id: String) = Photo(id, Uri.EMPTY, 10L, 100, 100, "image/jpeg")
}
