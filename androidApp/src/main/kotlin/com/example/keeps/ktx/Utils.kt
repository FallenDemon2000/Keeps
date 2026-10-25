package com.example.keeps.ktx

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellationException

internal fun <T> runOrNull(block: () -> T): T? =
    runCatching { block() }
        .onFailure { Log.e("Utils", "Error running block", it) }
        .getOrNull()

internal suspend fun <T> runCancellable(block: suspend () -> T): T? =
    runCatching { block() }
        .onFailure {
            if (it is CancellationException) throw it
            Log.e("Utils", "Error running block", it)
        }
        .getOrNull()

internal fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

internal fun Context.hasPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) ==
        PackageManager.PERMISSION_GRANTED
