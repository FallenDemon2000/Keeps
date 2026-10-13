package com.example.keeps.presentation.results

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.keeps.R
import com.example.keeps.ktx.hasPermission
import com.example.keeps.ktx.toast
import com.example.keeps.presentation.results.model.ResultsAction
import com.example.keeps.presentation.results.model.ResultsEvent
import kotlinx.coroutines.flow.Flow

/**
 * Wraps [onAction] so that [ResultsAction.DeleteSelected] first makes sure the
 * read-images permission (needed to resolve real MediaStore URIs) is granted:
 * if missing it is requested, and the "delete" is re-dispatched once granted.
 * Every other action is forwarded untouched.
 */
@Composable
fun rememberResultsActionHandler(
    onAction: (ResultsAction) -> Unit,
): (ResultsAction) -> Unit {
    val context = LocalContext.current
    val currentOnAction by rememberUpdatedState(onAction)
    val deniedMessage = stringResource(R.string.photo_access_required)

    val permission = readImagesPermission()
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            currentOnAction(ResultsAction.DeleteSelected)
        } else {
            context.toast(deniedMessage)
        }
    }

    return remember(context, permission, permissionLauncher) {
        return@remember { action ->
            val hasPermission = context.hasPermission(permission)
            if (action is ResultsAction.DeleteSelected && !hasPermission) {
                permissionLauncher.launch(permission)
            } else {
                currentOnAction(action)
            }
        }
    }
}

/**
 * Collects [ResultsEvent]s: launches the system delete confirmation for
 * [ResultsEvent.RequestDelete] (reporting [ResultsAction.DeleteConfirmed] on
 * approval) and shows a toast for [ResultsEvent.DeleteFailed].
 */
@Composable
fun ResultsEffects(
    events: Flow<ResultsEvent>,
    onAction: (ResultsAction) -> Unit,
) {
    val context = LocalContext.current
    val currentOnAction by rememberUpdatedState(onAction)
    val deleteFailedMessage = stringResource(R.string.delete_failed)
    var pendingDeleteIds by remember { mutableStateOf(emptySet<String>()) }
    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentOnAction(ResultsAction.DeleteConfirmed(pendingDeleteIds))
        }
        pendingDeleteIds = emptySet()
    }

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is ResultsEvent.RequestDelete -> {
                    pendingDeleteIds = event.photoIds
                    deleteLauncher.launch(IntentSenderRequest.Builder(event.intentSender).build())
                }

                ResultsEvent.DeleteFailed -> {
                    context.toast(deleteFailedMessage)
                }
            }
        }
    }
}

private fun readImagesPermission() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
