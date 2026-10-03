package com.example.keeps.presentation.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Full-width call-to-action button (btn-primary: 52dp height, 20dp radius).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KeepsTheme.colorScheme.primary,
            contentColor = KeepsTheme.colorScheme.onPrimary,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        Text(
            text = text,
            style = KeepsTheme.typography.bodyLarge.copy(fontSize = 15.sp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    KeepsTheme {
        PrimaryButton(
            text = "Primary",
            onClick = {},
        )
    }
}
