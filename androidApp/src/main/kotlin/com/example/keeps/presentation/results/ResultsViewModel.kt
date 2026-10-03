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
            is ResultsAction.TogglePhotoSelected -> togglePhotoSelected(action.photoId)
            is ResultsAction.SelectAllInGroup -> selectAllInGroup(action.groupId)
            is ResultsAction.SelectNoneInGroup -> selectNoneInGroup(action.groupId)
            ResultsAction.ClearSelection -> clearSelection()
            ResultsAction.DeleteSelected -> deleteSelected()
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

    private fun togglePhotoSelected(photoId: String) {
        _resultsState.update { current ->
            val updated = current.selectedPhotoIds.toMutableSet()
            if (!updated.add(photoId)) updated.remove(photoId)
            current.copy(selectedPhotoIds = updated)
        }
    }

    private fun selectAllInGroup(groupId: String) {
        val group = resultsState.value.groups.firstOrNull { it.id == groupId } ?: return
        val groupPhotoIds = group.photos.map { it.id }.toSet()

        _resultsState.update {
            it.copy(selectedPhotoIds = it.selectedPhotoIds + groupPhotoIds)
        }
    }

    private fun selectNoneInGroup(groupId: String) {
        val group = resultsState.value.groups.firstOrNull { it.id == groupId } ?: return
        val groupPhotoIds = group.photos.map { it.id }.toSet()

        _resultsState.update {
            it.copy(selectedPhotoIds = it.selectedPhotoIds - groupPhotoIds)
        }
    }

    private fun clearSelection() {
        _resultsState.update { it.copy(selectedPhotoIds = emptySet()) }
    }

    private fun deleteSelected() {
        val selected = _resultsState.value.selectedPhotoIds
        val updatedGroups = scanResultsRepository.groups.value
            .map { group -> group.copy(photos = group.photos.filterNot { it.id in selected }) }
            .filter { it.photos.isNotEmpty() }

        scanResultsRepository.setResults(updatedGroups)
        clearSelection()
    }
}
