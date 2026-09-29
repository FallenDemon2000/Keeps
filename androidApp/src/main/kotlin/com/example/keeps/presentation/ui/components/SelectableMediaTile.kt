package com.example.keeps.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.components.badges.KeepBadge
import com.example.keeps.presentation.ui.icons.Icons
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Square, selectable photo tile used in the Results photo grid (photo-cell spec):
 * a gradient placeholder background, a top-right selection checkmark, an optional
 * "keep" badge, and a bottom gradient metadata overlay.
 */
@Composable
@Suppress("LongMethod", "LongParameterList")
fun SelectableMediaTile(
    background: Brush,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    modifier: Modifier = Modifier,
    isKeepCandidate: Boolean = false,
    metadataText: String? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable(onClick = onToggleSelected),
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
            )
        }
        if (isKeepCandidate) {
            KeepBadge(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp))
        }
        if (metadataText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        ),
                    )
                    .padding(8.dp),
            ) {
                Text(
                    text = metadataText,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Black.copy(alpha = 0.4f)
                    },
                )
                .then(
                    if (!selected) {
                        Modifier.border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Check,
                    contentDescription = "Selected for deletion",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("MagicNumber")
private fun SelectableMediaTilePreview() {
    KeepsTheme(darkTheme = true) {
        Box(modifier = Modifier.size(180.dp)) {
            SelectableMediaTile(
                background = Brush.linearGradient(
                    listOf(Color(0xFF1A3A4A), Color(0xFF2D5A70), Color(0xFF1A4A3A)),
                ),
                selected = false,
                onToggleSelected = {},
                isKeepCandidate = true,
                metadataText = "4.2 MB · 4032\u00D73024",
            )
        }
    }
}
