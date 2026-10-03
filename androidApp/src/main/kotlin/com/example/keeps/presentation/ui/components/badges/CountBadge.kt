package com.example.keeps.presentation.ui.components.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Small circular count badge overlaid on a bottom-nav icon (nav-badge spec).
 */
@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(KeepsTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            style = KeepsTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = KeepsTheme.colorScheme.onPrimary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BadgesPreview() {
    KeepsTheme(darkTheme = true) {
        CountBadge(count = 3)
    }
}
