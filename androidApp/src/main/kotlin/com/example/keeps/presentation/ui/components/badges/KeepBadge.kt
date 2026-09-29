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
 * Small tag marking the best-quality photo to keep in a duplicate group (keep-badge spec).
 */
@Composable
fun KeepBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = "keep",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Preview
@Composable
private fun KeepBadgePreview() {
    KeepsTheme {
        KeepBadge()
    }
}
