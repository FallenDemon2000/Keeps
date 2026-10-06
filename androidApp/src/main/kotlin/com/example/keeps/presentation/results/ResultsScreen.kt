package com.example.keeps.presentation.results

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.keeps.R
import com.example.keeps.presentation.results.model.PhotoGroupUi
import com.example.keeps.presentation.ui.components.EmptyState
import com.example.keeps.presentation.ui.components.KeepsCard
import com.example.keeps.presentation.ui.components.PageHeader
import com.example.keeps.presentation.ui.components.SelectableMediaTile
import com.example.keeps.presentation.ui.components.badges.SimilarityBadge
import com.example.keeps.presentation.ui.components.buttons.ActionButton
import com.example.keeps.presentation.ui.components.buttons.XsButton
import com.example.keeps.presentation.ui.theme.KeepsTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Entry point for the Results tab: collects [ResultsViewModel] state and renders
 * [ResultsView].
 */
@Composable
fun ResultsScreen(
    onStartNewScan: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResultsViewModel = koinViewModel(),
) {
    val state by viewModel.resultsState.collectAsStateWithLifecycle()

    ResultsView(
        state = state,
        onAction = viewModel::onAction,
        onStartNewScan = onStartNewScan,
        modifier = modifier,
    )
}

@Composable
@Suppress("LongMethod")
private fun ResultsView(
    state: ResultsUiState,
    onAction: (ResultsAction) -> Unit,
    onStartNewScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        PageHeader(title = stringResource(id = R.string.results))
        if (state.isEmpty) {
            Box(modifier = Modifier.fillMaxSize()) {
                EmptyState(
                    icon = "\u2728",
                    title = "All clear!",
                    description = "No duplicates found among ${state.totalPhotoCount} photos " +
                        "at the current threshold.",
                    actionLabel = "Scan new photos",
                    onActionClick = onStartNewScan,
                )
            }
        } else {
            ResultsContent(
                state = state,
                onAction = onAction,
                onStartNewScan = onStartNewScan,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ResultsContent(
    state: ResultsUiState,
    onAction: (ResultsAction) -> Unit,
    onStartNewScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ResultsStats(
            groupCount = state.groups.size.toString(),
            totalPhotoCount = state.totalPhotoCount.toString(),
            onStartNewScan = onStartNewScan,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            val bottomPadding = if (state.selectedCount > 0) 120.dp else 24.dp
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp, 20.dp, 4.dp, bottomPadding),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(items = state.groups, key = { it.id }) { group ->
                    GroupCard(
                        group = group,
                        selectedPhotoIds = state.selectedPhotoIds.values.flatten(),
                        onAction = onAction,
                    )
                }
            }

            if (state.selectedCount > 0) {
                ActionBar(
                    selectedCount = state.selectedCount,
                    selectedBytes = state.selectedBytes,
                    onClear = { onAction(ResultsAction.ClearSelection) },
                    onDelete = { onAction(ResultsAction.DeleteSelected) },
                    onKeep = { onAction(ResultsAction.KeepSelected) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun ResultsStats(
    groupCount: String,
    totalPhotoCount: String,
    onStartNewScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Text(
                text = "$groupCount groups",
                style = KeepsTheme.typography.titleSmall,
            )
            Text(
                text = " \u00B7 $totalPhotoCount photos",
                style = KeepsTheme.typography.bodyMedium,
                color = KeepsTheme.colorScheme.onSurfaceVariant,
            )
        }
        ActionButton(
            text = "New scan",
            onClick = onStartNewScan,
            textColor = KeepsTheme.colorScheme.onSurfaceVariant,
            backgroundColor = KeepsTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun GroupCard(
    group: PhotoGroupUi,
    selectedPhotoIds: List<String>,
    onAction: (ResultsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KeepsCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SimilarityBadge(
                    percentText = "${group.similarityPercent}%",
                    isHighSimilarity = group.similarityPercent >= 90,
                )
                Text(
                    text = "${group.photos.size} similar photos",
                    style = KeepsTheme.typography.bodySmall,
                    color = KeepsTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                XsButton(
                    text = "All",
                    onClick = { onAction(ResultsAction.SelectAllInGroup(group.id)) },
                )
                XsButton(
                    text = "None",
                    onClick = { onAction(ResultsAction.SelectNoneInGroup(group.id)) },
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            group.photos.chunked(2).forEach { rowPhotos ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    rowPhotos.forEach { photo ->
                        SelectableMediaTile(
                            selected = photo.id in selectedPhotoIds,
                            onToggleSelected = {
                                onAction(ResultsAction.TogglePhotoSelected(group.id, photo.id))
                            },
                            imageUri = photo.imageUri,
                            metadataText = "${photo.sizeText} \u00B7 ${photo.dimensionsText}",
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowPhotos.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionBar(
    selectedCount: Int,
    selectedBytes: Long,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onKeep: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val freedMb = selectedBytes / 1_000_000.0
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(KeepsTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(text = "$selectedCount selected", style = KeepsTheme.typography.titleSmall)
            Text(
                text = "%.1f MB freed".format(freedMb),
                style = KeepsTheme.typography.bodySmall,
                color = KeepsTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionButton(
                text = "Clear",
                onClick = onClear,
                textColor = KeepsTheme.colorScheme.onSurfaceVariant,
                backgroundColor = KeepsTheme.colorScheme.surfaceVariant,
            )

            ActionButton(
                text = "Delete",
                onClick = onDelete,
                textColor = KeepsTheme.colorScheme.onError,
                backgroundColor = KeepsTheme.colorScheme.error,
            )

            ActionButton(
                text = "Keep",
                onClick = onKeep,
                textColor = KeepsTheme.colorScheme.onPrimary,
                backgroundColor = KeepsTheme.colorScheme.primary,
            )
        }
    }
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun ResultsPopulatedPreview() {
    KeepsTheme(darkTheme = false) {
        Scaffold {
            ResultsView(
                state = ResultsUiState(
                    groups = FakeResultsData.sampleGroups,
                    selectedPhotoIds = mapOf("group-1" to setOf("photo-1b", "photo-1c")),
                ),
                onAction = {},
                onStartNewScan = {},
            )
        }
    }
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun ResultsPopulatedDarkPreview() {
    KeepsTheme(darkTheme = true) {
        Scaffold {
            ResultsView(
                state = ResultsUiState(
                    groups = FakeResultsData.sampleGroups,
                    selectedPhotoIds = mapOf("group-1" to setOf("photo-1b", "photo-1c")),
                ),
                onAction = {},
                onStartNewScan = {},
            )
        }
    }
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun ResultsEmptyPreview() {
    KeepsTheme(darkTheme = false) {
        Scaffold {
            ResultsView(
                state = ResultsUiState(groups = emptyList()),
                onAction = {},
                onStartNewScan = {},
            )
        }
    }
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun ResultsEmptyDarkPreview() {
    KeepsTheme(darkTheme = true) {
        Scaffold {
            ResultsView(
                state = ResultsUiState(groups = emptyList()),
                onAction = {},
                onStartNewScan = {},
            )
        }
    }
}
