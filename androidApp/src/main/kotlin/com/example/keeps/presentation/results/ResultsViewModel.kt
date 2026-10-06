package com.example.keeps.presentation.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.presentation.toUiGroups
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Owns the Results screen's selection state over the latest scan's groups,
 * read from [ScanResultsRepository] (populated by `HomeViewModel`'s real photo
 * picker + placeholder grouping flow). "New scan"/delete actions only mutate
 * selection and the repository's in-memory groups — no photos are removed
 * from device storage yet (see `ScanResultsRepository` for that caveat).
 */
class ResultsViewModel(
    private val scanResultsRepository: ScanResultsRepository,
) : ViewModel() {

    private val _resultsState = MutableStateFlow(ResultsUiState())
    val resultsState: StateFlow<ResultsUiState> = _resultsState

    init {
        collectScanResultsChanges()
    }

    fun onAction(action: ResultsAction) {
        when (action) {
            is ResultsAction.SelectAllInGroup -> selectAllInGroup(action.groupId)
            is ResultsAction.SelectNoneInGroup -> selectNoneInGroup(action.groupId)
            is ResultsAction.ClearSelection -> clearSelection()
            is ResultsAction.DeleteSelected -> deleteSelected()
            is ResultsAction.KeepSelected -> keepSelected()
            is ResultsAction.TogglePhotoSelected ->
                togglePhotoSelected(action.groupId, action.photoId)
        }
    }

    private fun collectScanResultsChanges() {
        viewModelScope.launch {
            scanResultsRepository.groups.collect { groups ->
                _resultsState.update {
                    it.copy(groups = groups.toUiGroups())
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
        val group = resultsState.value.groups.firstOrNull { it.id == groupId } ?: return
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

    private fun deleteSelected() {
        val selected = _resultsState.value.selectedPhotoIds
        val updatedGroups = scanResultsRepository.groups.value
            .map { group ->
                val deleteIds = selected[group.id] ?: return@map group
                group.copy(photos = group.photos.filter { it.id !in deleteIds })
            }
            .filter { it.photos.isNotEmpty() }

        scanResultsRepository.setResults(updatedGroups)
        clearSelection()
    }

    private fun keepSelected() {
        val selected = _resultsState.value.selectedPhotoIds
        val updatedGroups = scanResultsRepository.groups.value
            .map { group ->
                val keepIds = selected[group.id] ?: return@map group
                group.copy(photos = group.photos.filter { it.id in keepIds })
            }
            .filter { it.photos.isNotEmpty() }

        scanResultsRepository.setResults(updatedGroups)
        clearSelection()
    }
}
