package com.example.keeps.presentation.results

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Owns the Results screen's selection state over a static mock dataset
 * ([FakeResultsData]). There is no real dedup engine yet, so "New scan" / delete
 * actions only mutate this in-memory mock state.
 */
class ResultsViewModel : ViewModel() {

    private val _state = MutableStateFlow(ResultsUiState(groups = FakeResultsData.sampleGroups))
    val state: StateFlow<ResultsUiState> = _state.asStateFlow()

    fun onAction(action: ResultsAction) {
        when (action) {
            is ResultsAction.TogglePhotoSelected -> togglePhotoSelected(action.photoId)
            is ResultsAction.SelectAllInGroup -> selectAllInGroup(action.groupId)
            is ResultsAction.SelectNoneInGroup -> selectNoneInGroup(action.groupId)
            ResultsAction.ClearSelection -> _state.update { it.copy(selectedPhotoIds = emptySet()) }
            ResultsAction.DeleteSelected -> deleteSelected()
        }
    }

    private fun togglePhotoSelected(photoId: String) {
        _state.update { current ->
            val updated = current.selectedPhotoIds.toMutableSet()
            if (!updated.add(photoId)) updated.remove(photoId)
            current.copy(selectedPhotoIds = updated)
        }
    }

    private fun selectAllInGroup(groupId: String) {
        _state.update { current ->
            val group = current.groups.firstOrNull { it.id == groupId } ?: return@update current
            current.copy(selectedPhotoIds = current.selectedPhotoIds + group.photos.map { it.id })
        }
    }

    private fun selectNoneInGroup(groupId: String) {
        _state.update { current ->
            val group = current.groups.firstOrNull { it.id == groupId } ?: return@update current
            val groupPhotoIds = group.photos.map { it.id }.toSet()
            current.copy(selectedPhotoIds = current.selectedPhotoIds - groupPhotoIds)
        }
    }

    private fun deleteSelected() {
        _state.update { current ->
            val selected = current.selectedPhotoIds
            val updatedGroups = current.groups
                .map { group -> group.copy(photos = group.photos.filterNot { it.id in selected }) }
                .filter { it.photos.isNotEmpty() }
            current.copy(groups = updatedGroups, selectedPhotoIds = emptySet())
        }
    }
}
