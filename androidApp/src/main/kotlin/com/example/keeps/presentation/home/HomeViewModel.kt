package com.example.keeps.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SCAN_STEP_DELAY_MS = 90L
private const val SCAN_STEP_INCREMENT = 0.08f

/**
 * Drives the Home screen's Upload -> (simulated) Scanning flow. There is no real
 * photo picker or dedup engine yet — choosing photos simply plays a simulated
 * progress animation and then signals [HomeEvent.ScanCompleted] so the app can
 * switch the bottom-nav selection to Results.
 */
class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    private var scanJob: Job? = null

    fun onChoosePhotosClicked() {
        if (_state.value is HomeUiState.Scanning) return
        scanJob = viewModelScope.launch {
            _state.update { HomeUiState.Scanning(0f) }
            var progress = 0f
            while (progress < 1f) {
                delay(SCAN_STEP_DELAY_MS)
                progress = (progress + SCAN_STEP_INCREMENT).coerceAtMost(1f)
                _state.update { HomeUiState.Scanning(progress) }
            }
            _state.update { HomeUiState.Idle }
            _events.send(HomeEvent.ScanCompleted)
        }
    }
}
