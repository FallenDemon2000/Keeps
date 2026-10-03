package com.example.keeps.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Uppercase mono caption used as a section header, e.g. within Settings
 * ("APPEARANCE", "DETECTION", "SMART SELECTION").
 */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        style = KeepsTheme.typography.labelMedium,
        color = KeepsTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(start = 2.dp, bottom = 8.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun SectionLabelPreview() {
    KeepsTheme(darkTheme = true) {
        SectionLabel(text = "Smart Selection")
    }
}
