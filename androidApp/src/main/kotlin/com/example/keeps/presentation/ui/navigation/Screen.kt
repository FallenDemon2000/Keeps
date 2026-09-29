package com.example.keeps.presentation.ui.navigation

import kotlinx.serialization.Serializable

/** Type-safe top-level destinations, one per bottom-nav tab. */
sealed interface Screen {

    @Serializable
    data object HomeScreen : Screen

    @Serializable
    data object ResultsScreen : Screen

    @Serializable
    data object SettingsScreen : Screen
}
