package com.example.keeps.presentation.settings

/**
 * User intents for the Settings screen (excluding theme mode, which is owned by
 * the app-root [com.example.keeps.presentation.ui.theme.ThemeViewModel]).
 */
sealed interface SettingsAction {
    data class ThresholdChanged(val value: Float) : SettingsAction
    data class GroupingMethodChanged(val method: GroupingMethod) : SettingsAction
    data class HighlightBestQualityChanged(val enabled: Boolean) : SettingsAction
    data class AutoSelectDuplicatesChanged(val enabled: Boolean) : SettingsAction
    data class ShowFileSizeChanged(val enabled: Boolean) : SettingsAction
    data class ShowDimensionsChanged(val enabled: Boolean) : SettingsAction
    data class SortOptionChanged(val option: SortOption) : SettingsAction
}
