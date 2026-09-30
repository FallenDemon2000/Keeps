package com.example.keeps.presentation.settings

/**
 * Detection grouping strategies (UI-only for now; not wired to a real engine).
 */
enum class GroupingMethod(val label: String) {
    Perceptual("Perceptual"),
    Exact("Exact"),
    Structural("Structural"),
}

/**
 * Results sort order (UI-only for now).
 */
enum class SortOption(val label: String) {
    Similarity("Similarity"),
    Size("Size"),
    Date("Date"),
}

/**
 * Renderable state for the Settings screen. Everything here is in-memory only for
 * this session; only the theme mode (owned by
 * [com.example.keeps.presentation.ui.theme.ThemeViewModel]) is passed in separately
 * since it must also drive the app's real Compose theme.
 */
data class SettingsUiState(
    val similarityThreshold: Float = 0.8f,
    val groupingMethod: GroupingMethod = GroupingMethod.Perceptual,
    val highlightBestQuality: Boolean = true,
    val autoSelectDuplicates: Boolean = false,
    val showFileSize: Boolean = true,
    val showDimensions: Boolean = true,
    val sortOption: SortOption = SortOption.Similarity,
)
