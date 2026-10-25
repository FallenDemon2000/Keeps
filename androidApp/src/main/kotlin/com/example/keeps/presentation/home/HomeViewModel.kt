package com.example.keeps.presentation.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.data.settings.SettingsRepository
import com.example.keeps.domain.usecase.StartPhotoScanUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the Home screen's pick-photos -> Scanning flow. [onPhotosPicked] hands
 * the picked URIs to [startPhotoScanUseCase], which keeps analyzing in the background.
 * The loading screen stays up (with real progress) until enough photos have been
 * analyzed and grouped for the first results to be worth showing
 * ([com.example.keeps.domain.model.ScanProgress.isReadyForFirstResults]), then
 * [HomeEvent.FirstResultsReady] navigates to Results, which keeps updating live.
 */
class HomeViewModel(
    private val startPhotoScanUseCase: StartPhotoScanUseCase,
    private val scanResultsRepository: ScanResultsRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    fun onPhotosPicked(uris: List<Uri>) {
        if (uris.isEmpty() || _state.value is HomeUiState.Scanning) return
        _state.update { HomeUiState.Scanning(0f) }

        viewModelScope.launch {
            val similarityThreshold = settingsRepository.getSimilarityThreshold()
            startPhotoScanUseCase(uris, similarityThreshold)

            scanResultsRepository.progress
                .onEach { progress ->
                    _state.update { HomeUiState.Scanning(progress.loadingFraction) }
                }
                .first { it.isReadyForFirstResults }

            _state.update { HomeUiState.Idle }
            _events.send(HomeEvent.FirstResultsReady)
        }
    }
}
