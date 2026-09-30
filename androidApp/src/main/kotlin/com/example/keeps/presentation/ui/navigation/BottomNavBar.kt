package com.example.keeps.presentation.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keeps.presentation.ui.components.badges.CountBadge
import com.example.keeps.presentation.ui.icons.Icons
import com.example.keeps.presentation.ui.theme.KeepsTheme

/**
 * App-wide 3-tab bottom navigation bar (bottom-nav / nav-tab spec).
 */
@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 8.dp, bottom = 24.dp),
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            val tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onItemSelected(index) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box {
                    Icon(imageVector = item.icon, contentDescription = item.label, tint = tint)
                    if (item.badgeCount != null && item.badgeCount > 0) {
                        CountBadge(
                            count = item.badgeCount,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(14.dp),
                        )
                    }
                }
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = tint,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    KeepsTheme(darkTheme = true) {
        BottomNavBar(
            items = listOf(
                BottomNavItem(label = "Home", icon = Icons.Home),
                BottomNavItem(label = "Results", icon = Icons.Search, badgeCount = 3),
                BottomNavItem(label = "Settings", icon = Icons.Settings),
            ),
            selectedIndex = 1,
            onItemSelected = {},
        )
    }
}
