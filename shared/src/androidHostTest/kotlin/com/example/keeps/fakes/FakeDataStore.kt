package com.example.keeps.fakes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDataStore(
    var fData: MutableStateFlow<Preferences> = MutableStateFlow(preferencesOf())
) : DataStore<Preferences> {

    override val data: Flow<Preferences>
        get() = fData

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences) =
        transform(fData.value).apply { fData.value = this }
}
