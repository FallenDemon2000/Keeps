package com.example.keeps.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * App-scoped settings that other layers need to read (currently only the
 * similarity threshold used when grouping photos), persisted across restarts.
 */
class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
) {

    val similarityThreshold: Flow<Float> = dataStore.data
        .map { it[SIMILARITY_THRESHOLD_KEY] ?: DEFAULT_SIMILARITY_THRESHOLD }
        .distinctUntilChanged()

    suspend fun setSimilarityThreshold(value: Float) {
        dataStore.edit { it[SIMILARITY_THRESHOLD_KEY] = value.coerceIn(0f, 1f) }
    }

    companion object {
        const val DEFAULT_SIMILARITY_THRESHOLD = 0.8f
        private val SIMILARITY_THRESHOLD_KEY = floatPreferencesKey("similarity_threshold")
    }
}
