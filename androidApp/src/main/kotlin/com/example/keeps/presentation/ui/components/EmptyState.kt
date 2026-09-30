package com.example.keeps.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keeps.presentation.ui.components.buttons.SecondaryButton
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Centered icon + title + description (+ optional CTA), used for "no duplicates
 * found" and similar empty results (empty-state spec).
 */
@Composable
@Suppress("LongParameterList")
fun EmptyState(
    icon: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = icon, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            SecondaryButton(text = actionLabel, onClick = onActionClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    KeepsTheme(darkTheme = true) {
        EmptyState(
            icon = "\u2728",
            title = "All clear!",
            description = "No duplicates found among 47 photos at the current threshold.",
            actionLabel = "Scan new photos",
            onActionClick = {},
        )
    }
}
