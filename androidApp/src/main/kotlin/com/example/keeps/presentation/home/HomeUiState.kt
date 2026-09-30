package com.example.keeps.presentation.home

/**
 * Renderable state for the Home screen (Upload idle vs. simulated Scanning).
 */
sealed interface HomeUiState {
    data object Idle : HomeUiState
    data class Scanning(val progress: Float) : HomeUiState
}
