package com.example.keeps.presentation.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.domain.usecase.GroupPhotosUseCase
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
 * Drives the Home screen's pick-photos -> Scanning flow. [onPhotosPicked] is
 * called with the URIs returned by the system photo picker (already capped at
 * [com.example.keeps.domain.usecase.MAX_SCAN_PHOTOS] by the picker itself):
 * their metadata is loaded, grouped via [groupPhotosUseCase] (currently a
 * hardcoded/random placeholder, not real similarity analysis), and the result
 * fully replaces whatever the previous scan produced in
 * [scanResultsRepository]. The progress animation is still simulated for now,
 * independent of the (fast, local) metadata-loading work.
 */
class HomeViewModel(
    private val groupPhotosUseCase: GroupPhotosUseCase,
    private val scanResultsRepository: ScanResultsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    fun onPhotosPicked(uris: List<Uri>) {
        if (uris.isEmpty() || _state.value is HomeUiState.Scanning) return
        viewModelScope.launch {
            _state.update { HomeUiState.Scanning(0f) }
            val groups = groupPhotosUseCase(uris)
            scanResultsRepository.setResults(groups)

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
