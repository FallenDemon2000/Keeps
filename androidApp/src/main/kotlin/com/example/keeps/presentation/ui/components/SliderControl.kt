package com.example.keeps.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Labeled slider with a header (label + formatted value), description, thumb/track,
 * and min/max footer captions, matching the `.slider-row` spec (used for the
 * similarity threshold control).
 */
@Composable
@Suppress("LongParameterList")
fun LabeledSlider(
    label: String,
    valueText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    description: String,
    minLabel: String,
    maxLabel: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = KeepsTheme.typography.bodyMedium)
            Text(
                text = valueText,
                style = KeepsTheme.typography.labelLarge,
                color = KeepsTheme.colorScheme.primary,
            )
        }
        Text(
            text = description,
            style = KeepsTheme.typography.bodySmall,
            color = KeepsTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = KeepsTheme.colorScheme.primary,
                activeTrackColor = KeepsTheme.colorScheme.primary,
                inactiveTrackColor = KeepsTheme.colorScheme.surfaceVariant,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = minLabel,
                style = KeepsTheme.typography.bodySmall,
                color = KeepsTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = maxLabel,
                style = KeepsTheme.typography.bodySmall,
                color = KeepsTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LabeledSliderPreview() {
    KeepsTheme(darkTheme = true) {
        LabeledSlider(
            label = "Similarity threshold",
            valueText = "80%",
            value = 0.8f,
            onValueChange = {},
            description = "Lower = more matches · Higher = near-identical only",
            minLabel = "50% loose",
            maxLabel = "100% exact",
            valueRange = 0.5f..1f,
        )
    }
}
