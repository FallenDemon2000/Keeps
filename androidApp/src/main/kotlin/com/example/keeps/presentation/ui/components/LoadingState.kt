package com.example.keeps.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.icons.Icons
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Centered icon + title + subtitle + determinate progress bar and percentage,
 * used for the "Scanning…" state (loading-state spec).
 */
@Composable
@Suppress("LongParameterList")
fun LoadingState(
    title: String,
    subtitle: String,
    progress: Float,
    progressPercentText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Search,
                contentDescription = "Search Icon",
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        KeepsLinearProgress(progress = progress, modifier = Modifier.width(220.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = progressPercentText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
private fun LoadingStatePreview() {
    KeepsTheme(darkTheme = false) {
        Scaffold {
            LoadingState(
                title = "Scanning...",
                subtitle = "Computing perceptual hashes",
                progress = 0.68f,
                progressPercentText = "68%",
            )
        }
    }
}

@Preview
@Composable
@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
private fun LoadingStateDarkPreview() {
    KeepsTheme(darkTheme = true) {
        Scaffold {
            LoadingState(
                title = "Scanning...",
                subtitle = "Computing perceptual hashes",
                progress = 0.68f,
                progressPercentText = "68%",
            )
        }
    }
}
