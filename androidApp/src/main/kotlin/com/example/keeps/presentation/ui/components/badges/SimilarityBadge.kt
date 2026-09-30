package com.example.keeps.presentation.ui.components.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Similarity percentage badge on a group card header (sim-badge high/med variants).
 * "High" (>=90%) uses a tinted primary background; other values use a neutral muted fill.
 */
@Composable
fun SimilarityBadge(
    percentText: String,
    isHighSimilarity: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isHighSimilarity) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = percentText,
            style = MaterialTheme.typography.labelMedium,
            color = if (isHighSimilarity) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}

@Preview
@Composable
private fun SimilarityBadgePreview() {
    KeepsTheme {
        SimilarityBadge(percentText = "72%", isHighSimilarity = false)
    }
}

@Preview
@Composable
private fun HighSimilarityBadgePreview() {
    KeepsTheme {
        SimilarityBadge(percentText = "92%", isHighSimilarity = true)
    }
}
