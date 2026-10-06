package com.example.keeps.presentation.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.icons.Icons
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Action button (btn-action: blue background, white text).
 */
@Composable
fun ActionButton(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.height(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp)) // Add some space between the icon and the text
        }
        Text(text = text, style = KeepsTheme.typography.titleSmall)
    }
}

@Preview
@Composable
private fun DeleteButtonPreview() {
    KeepsTheme {
        ActionButton(
            text = "Delete 4",
            textColor = KeepsTheme.colorScheme.onError,
            backgroundColor = KeepsTheme.colorScheme.error,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun KeepButtonPreview() {
    KeepsTheme {
        ActionButton(
            text = "Keep 4",
            textColor = KeepsTheme.colorScheme.onPrimary,
            backgroundColor = KeepsTheme.colorScheme.primary,
            onClick = {},
            leadingIcon = Icons.Check,
        )
    }
}
