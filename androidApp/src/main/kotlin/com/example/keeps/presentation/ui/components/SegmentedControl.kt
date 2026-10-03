package com.example.keeps.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * N-option segmented control (single-select pill group), matching the `.segmented` spec.
 */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(KeepsTheme.colorScheme.surfaceVariant)
            .padding(2.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selected) KeepsTheme.colorScheme.surface else Color.Transparent,
                    )
                    .selectable(selected = selected, onClick = { onOptionSelected(index) })
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = KeepsTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = if (selected) {
                        KeepsTheme.colorScheme.onSurface
                    } else {
                        KeepsTheme.colorScheme.onSurfaceVariant
                    },
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SegmentedControlPreview() {
    KeepsTheme(darkTheme = true) {
        SegmentedControl(
            options = listOf("Dark", "Light", "Auto"),
            selectedIndex = 0,
            onOptionSelected = {},
        )
    }
}
