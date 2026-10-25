package com.example.keeps.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/** Only one instance may exist per file; callers must keep it a singleton. */
expect fun createSettingsDataStore(path: String): DataStore<Preferences>
