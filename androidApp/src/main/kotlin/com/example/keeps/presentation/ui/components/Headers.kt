package com.example.keeps.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * Screen page header: title on the left, optional logo badge on the right
 * (page-header / logo-badge spec). Used at the top of Home/Results/Settings.
 */
@Composable
fun PageHeader(
    title: String,
    modifier: Modifier = Modifier,
    showLogoBadge: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = KeepsTheme.typography.titleLarge)
        if (showLogoBadge) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(KeepsTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u25C8",
                    style = KeepsTheme.typography.titleSmall,
                    color = KeepsTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PageHeaderPreview() {
    KeepsTheme(darkTheme = true) {
        PageHeader(title = "Keeps", showLogoBadge = true)
    }
}
