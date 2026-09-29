package com.example.keeps.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.keeps.R

val DMSans = FontFamily(
    Font(R.font.dm_sans_variable, FontWeight.Normal),
    Font(R.font.dm_sans_variable, FontWeight.Medium),
    Font(R.font.dm_sans_variable, FontWeight.SemiBold),
    Font(R.font.dm_sans_variable, FontWeight.Bold),
)

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_variable, FontWeight.SemiBold),
    Font(R.font.jetbrains_mono_variable, FontWeight.Bold),
    Font(R.font.jetbrains_mono_variable, FontWeight.ExtraBold),
)

/**
 * Type scale mapped to every text role used across the Home/Results/Settings screens
 * (see `specs/keeps-design.html`). Only the roles actually used are overridden; any
 * Material3 slot not listed here keeps its default value.
 */
val Typography = Typography(
    // Page header title, e.g. "Dedup" / "Results" / "Settings" (page-header-title: 20/700)
    titleLarge = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.02).sp,
    ),
    // Upload hero headline, two lines (upload-hero-title: 24/700, line-height 1.2)
    headlineSmall = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.8.sp,
        letterSpacing = (-0.02).sp,
    ),
    // Empty/loading state section headings (empty-title/loading-title: 20/600)
    titleMedium = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    // Card/section titles, drop-zone title (drop-title: 16/600)
    bodyLarge = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    // Results summary text (results-summary-text: 14/600)
    titleSmall = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Settings row labels / slider labels (settings-row-label, slider-label: 14/500)
    bodyMedium = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Body/description copy (hero desc, feature pills, settings desc, empty/loading sub: 13/400, line-height 1.6)
    bodySmall = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.8.sp,
    ),
    // Mono data values: status time, slider value, progress percentage (13/600)
    labelLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    // Mono captions: settings section labels (uppercase, tracking 0.15em) and similarity badge (11/600)
    labelMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.15.sp,
    ),
    // Mono micro labels: keep badge, photo metadata overlay (10/600)
    labelSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 13.sp,
    ),
)
