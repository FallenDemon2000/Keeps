package com.example.keeps.presentation.results.model

/**
 * User intents for the Results screen.
 */
sealed interface ResultsAction {
    data class TogglePhotoSelected(val groupId: String, val photoId: String) : ResultsAction
    data class SelectAllInGroup(val groupId: String) : ResultsAction
    data class SelectNoneInGroup(val groupId: String) : ResultsAction
    data object ClearSelection : ResultsAction
    data object DeleteSelected : ResultsAction
    data object KeepSelected : ResultsAction
    data class DeleteConfirmed(val photoIds: Set<String>) : ResultsAction
}
