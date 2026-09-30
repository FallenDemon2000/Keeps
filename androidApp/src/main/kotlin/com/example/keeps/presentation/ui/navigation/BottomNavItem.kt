package com.example.keeps.presentation.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A single destination in the [BottomNavBar] (icon, label, and an optional
 * unread/result count badge).
 */
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int? = null,
)
