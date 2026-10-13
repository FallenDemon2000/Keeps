package com.example.keeps.presentation.results.model

import android.content.IntentSender

/**
 * One-time events for the Results screen.
 */
sealed interface ResultsEvent {

    /** Launch the system delete confirmation; [photoIds] are removed on approval. */
    data class RequestDelete(
        val intentSender: IntentSender,
        val photoIds: Set<String>,
    ) : ResultsEvent

    data object DeleteFailed : ResultsEvent
}
