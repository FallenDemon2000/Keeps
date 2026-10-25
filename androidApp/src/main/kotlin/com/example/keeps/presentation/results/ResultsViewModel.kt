package com.example.keeps.presentation.results

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.domain.model.GroupKind
import com.example.keeps.domain.model.PhotoGroup
import com.example.keeps.domain.usecase.CreateDeleteRequestUseCase
import com.example.keeps.ktx.runOrNull
import com.example.keeps.presentation.results.model.ResultsAction
import com.example.keeps.presentation.results.model.ResultsEvent
import com.example.keeps.presentation.results.model.ResultsUiState
import com.example.keeps.presentation.toUiGroups
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Owns the Results screen's selection state over the latest scan's groups,
 * read from [ScanResultsRepository] (populated progressively by the background scan;
 * Delete/Keep are ignored until the scan completes). Deleting is a two-step flow: the
 * selected photos' deletion is requested via [ResultsEvent.RequestDelete]
 * (system confirmation dialog); only once the UI reports
 * [ResultsAction.DeleteConfirmed] are they removed from the in-memory groups.
 * Cancelling leaves the list and selection untouched.
 */
class ResultsViewModel(
    private val scanResultsRepository: ScanResultsRepository,
    private val createDeleteRequest: CreateDeleteRequestUseCase,
) : ViewModel() {

    private val _events = Channel<ResultsEvent>(Channel.BUFFERED)
    val events: Flow<ResultsEvent> = _events.receiveAsFlow()

    private val _resultsState = MutableStateFlow(ResultsUiState())
    val resultsState: StateFlow<ResultsUiState> = _resultsState.asStateFlow()

    init {
        collectScanResultsChanges()
        collectScanProgress()
    }

    fun onAction(action: ResultsAction) {
        when (action) {
            is ResultsAction.SelectAllInGroup -> selectAllInGroup(action.groupId)
            is ResultsAction.SelectNoneInGroup -> selectNoneInGroup(action.groupId)
            is ResultsAction.ClearSelection -> clearSelection()
            is ResultsAction.DeleteSelected -> requestDelete()
            is ResultsAction.KeepSelected -> requestKeep()
            is ResultsAction.DeleteConfirmed -> removeDeleted(action.photoIds)
            is ResultsAction.TogglePhotoSelected ->
                togglePhotoSelected(action.groupId, action.photoId)
        }
    }

    private fun collectScanResultsChanges() {
        viewModelScope.launch {
            scanResultsRepository.results.collect { results ->
                _resultsState.update {
                    it.copy(
                        groups = results.groups.toUiGroups(),
                        similarityThreshold = (results.similarityThreshold * 100).toInt(),
                        selectedPhotoIds = it.selectedPhotoIds.remappedTo(results.groups),
                    )
                }
            }
        }
    }

    private fun collectScanProgress() {
        viewModelScope.launch {
            scanResultsRepository.progress.collect { progress ->
                _resultsState.update {
                    it.copy(
                        isAnalyzing = !progress.isComplete,
                        analyzedFraction = progress.fraction,
                    )
                }
            }
        }
    }

    private fun togglePhotoSelected(groupId: String, photoId: String) {
        _resultsState.update { current ->
            val updated = current.selectedPhotoIds.toMutableMap()
            when {
                !updated.containsKey(groupId) ->
                    updated[groupId] = mutableSetOf(photoId)

                !updated[groupId]!!.contains(photoId) ->
                    updated[groupId] = updated[groupId]!! + photoId

                else ->
                    updated[groupId] = updated[groupId]!! - photoId
            }

            current.copy(selectedPhotoIds = updated)
        }
    }

    private fun selectAllInGroup(groupId: String) {
        val group = _resultsState.value.groups.firstOrNull { it.id == groupId } ?: return
        val groupPhotoIds = group.photos.map { it.id }.toSet()
        val updated = _resultsState.value.selectedPhotoIds.plus(groupId to groupPhotoIds)

        _resultsState.update { it.copy(selectedPhotoIds = updated) }
    }

    private fun selectNoneInGroup(groupId: String) {
        val updated = _resultsState.value.selectedPhotoIds.minus(groupId)
        _resultsState.update { it.copy(selectedPhotoIds = updated) }
    }

    private fun clearSelection() {
        _resultsState.update { it.copy(selectedPhotoIds = emptyMap()) }
    }

    private fun requestDelete() {
        if (_resultsState.value.isAnalyzing) return
        val selectedIds = _resultsState.value.selectedPhotoIds.values.flatten().toSet()
        val photos = scanResultsRepository.results.value.groups
            .flatMap { it.photos }
            .filter { it.id in selectedIds }

        if (photos.isEmpty()) return
        sendDeleteRequestEvent(photos.map { it.uri })
    }

    private fun requestKeep() {
        if (_resultsState.value.isAnalyzing) return
        val selected = _resultsState.value.selectedPhotoIds
        val photos = scanResultsRepository.results.value.groups
            .filter { it.kind == GroupKind.Similar }
            .flatMap { group ->
                val keepIds = selected[group.id] ?: return@flatMap emptyList()
                group.photos.filter { it.id !in keepIds }
            }

        if (photos.isEmpty()) return
        sendDeleteRequestEvent(photos.map { it.uri })
    }

    private fun removeDeleted(photoIds: Set<String>) {
        val results = scanResultsRepository.results.value
        val updatedGroups = results.groups
            .map { group -> group.copy(photos = group.photos.filter { it.id !in photoIds }) }
            .filter { it.photos.isNotEmpty() }

        scanResultsRepository.setResults(updatedGroups, results.similarityThreshold)
        clearSelection()
    }

    private fun sendDeleteRequestEvent(uris: List<Uri>) {
        val event = runOrNull {
            ResultsEvent.RequestDelete(
                intentSender = createDeleteRequest(uris),
                photoIds = uris.map { it.toString() }.toSet(),
            )
        }

        _events.trySend(event ?: ResultsEvent.DeleteFailed)
    }

    /**
     * Groups merge and change while a scan is running, so selected photos are
     * re-attached to whichever group now contains them (dropping vanished ones).
     */
    private fun Map<String, Set<String>>.remappedTo(
        groups: List<PhotoGroup>,
    ): Map<String, Set<String>> {
        val selected = values.flatten().toSet()
        if (selected.isEmpty()) return emptyMap()

        return groups.mapNotNull { group ->
            val ids = group.photos.map { it.id }.filter { it in selected }.toSet()
            if (ids.isEmpty()) null else group.id to ids
        }.toMap()
    }
}
