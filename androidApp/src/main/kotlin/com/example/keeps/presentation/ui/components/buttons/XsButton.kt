package com.example.keeps.presentation.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keeps.presentation.ui.theme.KeepsTheme
import androidx.compose.material3.Button as Material3Button

/**
 * Extra-small pill button used inline within group headers (btn-xs, e.g. "All" / "None").
 */
@Composable
fun XsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Material3Button(
        onClick = onClick,
        modifier = modifier.height(28.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KeepsTheme.colorScheme.surfaceVariant,
            contentColor = KeepsTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 10.dp),
    ) {
        Text(text = text, style = KeepsTheme.typography.bodyMedium.copy(fontSize = 11.sp))
    }
}

@Preview
@Composable
private fun ButtonsPreview() {
    KeepsTheme(darkTheme = true) {
        XsButton(
            text = "All",
            onClick = {},
        )
    }
}
