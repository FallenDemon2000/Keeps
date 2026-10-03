package com.example.keeps.presentation.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keeps.R
import com.example.keeps.presentation.ui.components.KeepsCard
import com.example.keeps.presentation.ui.components.KeepsToggleSwitch
import com.example.keeps.presentation.ui.components.LabeledSlider
import com.example.keeps.presentation.ui.components.PageHeader
import com.example.keeps.presentation.ui.components.SectionLabel
import com.example.keeps.presentation.ui.components.SegmentedControl
import com.example.keeps.presentation.ui.theme.KeepsTheme
import com.example.keeps.presentation.ui.theme.ThemeMode

/**
 * Entry point for the Settings tab: collects [SettingsViewModel] state and the
 * app-root theme mode, and renders [SettingsView].
 */
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsView(
        state = state,
        themeMode = themeMode,
        onThemeModeChange = onThemeModeChange,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

private val ThemeModeOptions = listOf(ThemeMode.Dark, ThemeMode.Light, ThemeMode.Auto)
private val GroupingOptions = GroupingMethod.entries
private val SortOptions = SortOption.entries

@Composable
@Suppress("LongMethod")
private fun SettingsView(
    state: SettingsUiState,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        PageHeader(title = stringResource(id = R.string.settings))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                SettingsSection(title = "Appearance") {
                    SettingsRow(label = "Theme", description = "App color scheme") {
                        SegmentedControl(
                            options = ThemeModeOptions.map { it.name },
                            selectedIndex = ThemeModeOptions.indexOf(themeMode),
                            onOptionSelected = { onThemeModeChange(ThemeModeOptions[it]) },
                            modifier = Modifier.size(width = 160.dp, height = 32.dp),
                        )
                    }
                }
            }
            item {
                SettingsSection(title = "Detection") {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        LabeledSlider(
                            label = "Similarity threshold",
                            valueText = "${(state.similarityThreshold * 100).toInt()}%",
                            value = state.similarityThreshold,
                            onValueChange = {
                                onAction(SettingsAction.ThresholdChanged(it))
                            },
                            description = "Lower = more matches \u00B7 Higher = " +
                                "near-identical only",
                            minLabel = "50% loose",
                            maxLabel = "100% exact",
                            valueRange = 0.5f..1f,
                        )
                    }
                    SettingsDivider()
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Grouping method", style = KeepsTheme.typography.bodyMedium)
                        SegmentedControl(
                            options = GroupingOptions.map { it.label },
                            selectedIndex = GroupingOptions.indexOf(state.groupingMethod),
                            onOptionSelected = {
                                onAction(SettingsAction.GroupingMethodChanged(GroupingOptions[it]))
                            },
                            modifier = Modifier.padding(top = 10.dp),
                        )
                    }
                }
            }
            item {
                SettingsSection(title = "Smart Selection") {
                    SettingsRow(
                        label = "Highlight best quality",
                        description = "Mark highest-resolution photo in each group",
                    ) {
                        KeepsToggleSwitch(
                            checked = state.highlightBestQuality,
                            onCheckedChange = {
                                onAction(SettingsAction.HighlightBestQualityChanged(it))
                            },
                        )
                    }
                    SettingsDivider()
                    SettingsRow(
                        label = "Auto-select duplicates",
                        description = "CheckIcon lower-res copies for deletion automatically",
                    ) {
                        KeepsToggleSwitch(
                            checked = state.autoSelectDuplicates,
                            onCheckedChange = {
                                onAction(SettingsAction.AutoSelectDuplicatesChanged(it))
                            },
                        )
                    }
                }
            }
            item {
                SettingsSection(title = "Display") {
                    SettingsRow(
                        label = "Show file size",
                        description = "Display bytes below each photo",
                    ) {
                        KeepsToggleSwitch(
                            checked = state.showFileSize,
                            onCheckedChange = { onAction(SettingsAction.ShowFileSizeChanged(it)) },
                        )
                    }
                    SettingsDivider()
                    SettingsRow(
                        label = "Show dimensions",
                        description = "Display pixel resolution",
                    ) {
                        KeepsToggleSwitch(
                            checked = state.showDimensions,
                            onCheckedChange = {
                                onAction(SettingsAction.ShowDimensionsChanged(it))
                            },
                        )
                    }
                    SettingsDivider()
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Sort groups by", style = KeepsTheme.typography.bodyMedium)
                        SegmentedControl(
                            options = SortOptions.map { it.label },
                            selectedIndex = SortOptions.indexOf(state.sortOption),
                            onOptionSelected = {
                                onAction(SettingsAction.SortOptionChanged(SortOptions[it]))
                            },
                            modifier = Modifier.padding(top = 10.dp),
                        )
                    }
                }
            }
            item {
                SettingsSection(title = "About") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(KeepsTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "\u25C8", color = KeepsTheme.colorScheme.onPrimary)
                        }
                        Column {
                            Text(text = "Dedup", style = KeepsTheme.typography.bodyMedium)
                            Text(
                                text = "Version 1.0 \u00B7 All processing on-device",
                                style = KeepsTheme.typography.bodySmall,
                                color = KeepsTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            item { Box(modifier = Modifier.padding(bottom = 12.dp)) }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        SectionLabel(text = title)
        KeepsCard(content = content)
    }
}

@Composable
private fun SettingsRow(
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailingContent: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = KeepsTheme.typography.bodyMedium)
            if (description != null) {
                Text(
                    text = description,
                    style = KeepsTheme.typography.bodySmall,
                    color = KeepsTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        trailingContent()
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = KeepsTheme.colorScheme.outline,
    )
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun SettingsPreview() {
    KeepsTheme(darkTheme = false) {
        Scaffold {
            SettingsView(
                state = SettingsUiState(),
                themeMode = ThemeMode.Dark,
                onThemeModeChange = {},
                onAction = {},
            )
        }
    }
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun SettingsDarkPreview() {
    KeepsTheme(darkTheme = true) {
        Scaffold {
            SettingsView(
                state = SettingsUiState(),
                themeMode = ThemeMode.Light,
                onThemeModeChange = {},
                onAction = {},
            )
        }
    }
}
