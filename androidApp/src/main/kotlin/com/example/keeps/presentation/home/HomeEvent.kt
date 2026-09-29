package com.example.keeps.presentation.home

/**
 * One-time effects for the Home screen.
 */
sealed interface HomeEvent {
    data object ScanCompleted : HomeEvent
}
