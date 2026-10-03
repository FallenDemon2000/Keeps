package com.example.keeps.presentation.ui.components

import android.net.Uri
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.keeps.presentation.ui.icons.Icons
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Square, selectable photo tile used in the Results photo grid (photo-cell spec):
 * a gradient placeholder background, a top-right selection checkmark, an optional
 * "keep" badge, and a bottom gradient metadata overlay. When [imageUri] is set
 * (real, picked photos), the actual thumbnail is drawn over [background] via
 * Coil; [background] alone is used for mock/preview data with no real image.
 */
@Composable
fun SelectableMediaTile(
    imageUri: Uri,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    modifier: Modifier = Modifier,
    metadataText: String? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(KeepsTheme.gradients.placeholder)
            .clickable(onClick = onToggleSelected),
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
            )
        }
        if (metadataText != null) {
            MediaMetadataOverlay(
                metadataText = metadataText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(KeepsTheme.gradients.shadow)
                    .padding(8.dp),
            )
        }
        MediaCheckBox(
            isSelected = selected,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp),
        )
    }
}

@Composable
private fun MediaCheckBox(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    var backgroundColor = KeepsTheme.colorScheme.primary
    var borderColor = KeepsTheme.colorScheme.primary

    if (isSelected) {
        backgroundColor = Color.Black.copy(alpha = 0.4f)
        borderColor = Color.White.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Check,
                contentDescription = "Selected for deletion",
                tint = KeepsTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun MediaMetadataOverlay(
    metadataText: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Text(
            text = metadataText,
            style = KeepsTheme.typography.labelSmall,
            color = Color.White,
        )
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("MagicNumber")
private fun SelectableMediaTilePreview() {
    KeepsTheme(darkTheme = true) {
        Box(modifier = Modifier.size(180.dp)) {
            SelectableMediaTile(
                imageUri = Uri.EMPTY,
                selected = false,
                onToggleSelected = {},
                metadataText = "4.2 MB · 4032\u00D73024",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("MagicNumber")
private fun SelectableMediaTileCheckedPreview() {
    KeepsTheme(darkTheme = true) {
        Box(modifier = Modifier.size(180.dp)) {
            SelectableMediaTile(
                imageUri = Uri.EMPTY,
                selected = true,
                onToggleSelected = {},
                metadataText = "4.2 MB · 4032\u00D73024",
            )
        }
    }
}
