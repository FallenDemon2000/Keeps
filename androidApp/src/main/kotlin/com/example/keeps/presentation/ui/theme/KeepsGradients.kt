package com.example.keeps.presentation.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val keepsGradients = AppGradients(
    placeholder = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A3A4A),
            Color(0xFF2D5A70),
            Color(0xFF1A4A3A),
        ),
    ),
    shadow = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.7f),
        ),
    ),
)

val LocalKeepsGradients = staticCompositionLocalOf { keepsGradients }

@Immutable
data class AppGradients(
    val placeholder: Brush,
    val shadow: Brush,
)
