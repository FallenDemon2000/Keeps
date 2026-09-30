package com.example.keeps.presentation.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Owns the in-memory (session-only) Settings state, aside from theme mode.
 */
class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun onAction(action: SettingsAction) {
        _state.update { current ->
            when (action) {
                is SettingsAction.ThresholdChanged ->
                    current.copy(similarityThreshold = action.value)

                is SettingsAction.GroupingMethodChanged ->
                    current.copy(groupingMethod = action.method)

                is SettingsAction.HighlightBestQualityChanged ->
                    current.copy(highlightBestQuality = action.enabled)

                is SettingsAction.AutoSelectDuplicatesChanged ->
                    current.copy(autoSelectDuplicates = action.enabled)

                is SettingsAction.ShowFileSizeChanged ->
                    current.copy(showFileSize = action.enabled)

                is SettingsAction.ShowDimensionsChanged ->
                    current.copy(showDimensions = action.enabled)

                is SettingsAction.SortOptionChanged ->
                    current.copy(sortOption = action.option)
            }
        }
    }
}
