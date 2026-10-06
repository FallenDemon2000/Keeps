package com.example.keeps

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keeps.presentation.ui.navigation.KeepsNavHost
import com.example.keeps.presentation.ui.theme.KeepsTheme
import com.example.keeps.presentation.ui.theme.ThemeViewModel
import com.example.keeps.presentation.ui.theme.resolveDarkTheme

/**
 * App root: owns the app-wide [ThemeViewModel] (the one piece of state shared
 * across tabs), resolves dark/light mode, wraps [KeepsTheme], and hosts
 * [KeepsNavHost].
 */
@Composable
fun KeepsApp(themeViewModel: ThemeViewModel = viewModel()) {
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
    val darkTheme = themeMode.resolveDarkTheme(isSystemInDarkTheme())

    KeepsTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = KeepsTheme.colorScheme.background,
        ) {
            KeepsNavHost(
                themeMode = themeMode,
                onThemeModeChange = themeViewModel::setThemeMode,
            )
        }
    }
}
