package com.example.keeps.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keeps.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Owns the in-memory (session-only) Settings state, aside from theme mode. The
 * similarity threshold lives in [SettingsRepository] (persisted) because photo
 * grouping reads it.
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.similarityThreshold.collect { threshold ->
                _state.update { it.copy(similarityThreshold = threshold) }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.ThresholdChanged -> setThresholdChanged(action.value)

            is SettingsAction.GroupingMethodChanged ->
                _state.update { it.copy(groupingMethod = action.method) }

            is SettingsAction.HighlightBestQualityChanged ->
                _state.update { it.copy(highlightBestQuality = action.enabled) }

            is SettingsAction.AutoSelectDuplicatesChanged ->
                _state.update { it.copy(autoSelectDuplicates = action.enabled) }

            is SettingsAction.ShowFileSizeChanged ->
                _state.update { it.copy(showFileSize = action.enabled) }

            is SettingsAction.ShowDimensionsChanged ->
                _state.update { it.copy(showDimensions = action.enabled) }

            is SettingsAction.SortOptionChanged ->
                _state.update { it.copy(sortOption = action.option) }
        }
    }

    private fun setThresholdChanged(value: Float) {
        viewModelScope.launch { settingsRepository.setSimilarityThreshold(value) }
    }
}
