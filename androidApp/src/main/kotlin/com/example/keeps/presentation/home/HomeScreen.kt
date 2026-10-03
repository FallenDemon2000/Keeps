package com.example.keeps.presentation.home

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.keeps.R
import com.example.keeps.domain.usecase.MAX_SCAN_PHOTOS
import com.example.keeps.presentation.ui.components.LoadingState
import com.example.keeps.presentation.ui.components.PageHeader
import com.example.keeps.presentation.ui.components.buttons.PrimaryButton
import com.example.keeps.presentation.ui.theme.KeepsTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Entry point for the Home tab: collects [HomeViewModel] state, launches the
 * system photo picker (capped at [MAX_SCAN_PHOTOS] images, local-only — no
 * network upload), observes the one-time scan-completed event, and renders
 * [HomeView].
 */
@Composable
fun HomeScreen(
    onScanCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pickPhotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_SCAN_PHOTOS),
    ) { uris -> viewModel.onPhotosPicked(uris) }

    val onChoosePhotosClicked = {
        val request = PickVisualMediaRequest(
            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
        )
        pickPhotos.launch(request)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                HomeEvent.ScanCompleted -> onScanCompleted()
            }
        }
    }

    HomeView(
        state = state,
        onChoosePhotosClicked = onChoosePhotosClicked,
        modifier = modifier,
    )
}

@Composable
private fun HomeView(
    state: HomeUiState,
    onChoosePhotosClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PageHeader(title = stringResource(id = R.string.app_name), showLogoBadge = true)
        when (state) {
            is HomeUiState.Scanning -> {
                LoadingState(
                    title = "Scanning\u2026",
                    subtitle = "Computing perceptual hashes",
                    progress = state.progress,
                    progressPercentText = "${(state.progress * 100).toInt()}%",
                )
            }

            is HomeUiState.Idle -> {
                HomeContent(
                    onChoosePhotosClicked = onChoosePhotosClicked,
                    modifier = Modifier,
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    onChoosePhotosClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column {
            Text(
                text = "Find & remove\nduplicate photos",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Upload photos and Dedup will group visually similar images so " +
                    "you can choose which ones to delete.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        SelectPhotosPlaceholder(modifier = Modifier.fillMaxWidth())
        PrimaryButton(text = "Choose Photos", onClick = onChoosePhotosClicked)
        FeaturesView(verticalArrangement = Arrangement.spacedBy(10.dp))
    }
}

@Composable
private fun SelectPhotosPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 40.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "\uD83D\uDDBC\uFE0F", fontSize = 28.sp)
        }
        Text(
            text = "Tap to select photos",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            text = "JPEG · PNG · WEBP · HEIC",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FeaturesView(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
) {
    Column(modifier = modifier, verticalArrangement = verticalArrangement) {
        FeatureCard(
            icon = "\u26A1",
            label = "Instant perceptual analysis",
            modifier = Modifier.fillMaxWidth(),
        )
        FeatureCard(
            icon = "\uD83C\uDFAF",
            label = "Adjustable similarity threshold",
            modifier = Modifier.fillMaxWidth(),
        )
        FeatureCard(
            icon = "\uD83D\uDD12",
            label = "Everything stays on your device",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = icon, fontSize = 18.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun HomeViewDarkPreview() {
    KeepsTheme(darkTheme = true) {
        Scaffold {
            HomeView(
                state = HomeUiState.Idle,
                onChoosePhotosClicked = {},
            )
        }
    }
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun HomeViewPreview() {
    KeepsTheme(darkTheme = false) {
        Scaffold {
            HomeView(
                state = HomeUiState.Idle,
                onChoosePhotosClicked = {},
            )
        }
    }
}

@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun HomeScanningPreview() {
    KeepsTheme {
        Scaffold {
            HomeView(
                state = HomeUiState.Scanning(progress = 0.68f),
                onChoosePhotosClicked = {},
            )
        }
    }
}
