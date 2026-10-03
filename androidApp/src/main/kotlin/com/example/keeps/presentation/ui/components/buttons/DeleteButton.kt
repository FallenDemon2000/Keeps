package com.example.keeps.presentation.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Destructive action button (btn-delete: red background, white text).
 */
@Composable
fun DeleteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KeepsTheme.colorScheme.error,
            contentColor = KeepsTheme.colorScheme.onError,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        Text(text = text, style = KeepsTheme.typography.titleSmall)
    }
}

@Preview
@Composable
private fun DeleteButtonPreview() {
    KeepsTheme {
        DeleteButton(
            text = "Delete",
            onClick = {},
        )
    }
}
