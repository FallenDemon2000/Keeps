package com.example.keeps.presentation.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * App-root-scoped view model holding the user's theme preference (Settings ->
 * Appearance -> Theme). It lives above the [androidx.navigation.NavHost] so it can
 * both wrap the app in [KeepsTheme]
 * and be read/updated from the Settings screen.
 */
class ThemeViewModel : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.Auto)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.update { mode }
    }
}

/**
 * Resolves whether the app should render in dark mode for the given [mode] and the
 * current system dark-theme flag.
 */
fun ThemeMode.resolveDarkTheme(systemInDarkTheme: Boolean): Boolean = when (this) {
    ThemeMode.Dark -> true
    ThemeMode.Light -> false
    ThemeMode.Auto -> systemInDarkTheme
}
