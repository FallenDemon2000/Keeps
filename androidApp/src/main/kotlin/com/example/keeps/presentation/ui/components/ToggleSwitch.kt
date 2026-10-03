package com.example.keeps.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.theme.KeepsTheme

private val TrackWidth = 48.dp
private val TrackHeight = 24.dp
private val ThumbSize = 18.dp
private val ThumbInset = 3.dp

/**
 * Pill-shaped on/off switch (48x24dp, 18dp thumb), matching the settings toggle spec.
 */
@Composable
fun KeepsToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) {
            KeepsTheme.colorScheme.primary
        } else {
            KeepsTheme.colorScheme.outline
        },
        label = "toggleTrackColor",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TrackWidth - ThumbSize - ThumbInset else ThumbInset,
        label = "toggleThumbOffset",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(width = TrackWidth, height = TrackHeight)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = { onCheckedChange(!checked) },
            )
            .semantics {
                role = Role.Switch
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
            },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(x = thumbOffset.roundToPx(), y = 0) }
                .size(ThumbSize)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KeepsToggleSwitchPreview() {
    KeepsTheme(darkTheme = true) {
        KeepsToggleSwitch(checked = true, onCheckedChange = {})
    }
}
