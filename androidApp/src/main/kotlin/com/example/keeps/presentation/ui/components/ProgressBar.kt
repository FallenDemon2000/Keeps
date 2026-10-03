package com.example.keeps.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Determinate linear progress track (progress-track/progress-fill spec), used by
 * [LoadingState] to show scan progress.
 */
@Composable
fun KeepsLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(KeepsTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(100.dp))
                .background(KeepsTheme.colorScheme.primary),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KeepsLinearProgressPreview() {
    KeepsTheme(darkTheme = true) {
        Box(modifier = Modifier.width(220.dp)) {
            KeepsLinearProgress(progress = 0.68f)
        }
    }
}
