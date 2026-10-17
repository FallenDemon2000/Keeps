package com.example.keeps.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.keeps.data.media.MediaStorePhotoRepository
import com.example.keeps.data.media.PhotoDataSource
import com.example.keeps.data.media.PhotoRepository
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.data.settings.createSettingsDataStore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    // Data Source
    single<PhotoDataSource> {
        PhotoDataSource(
            contentResolver = get<Context>().contentResolver,
        )
    }

    // Repository
    singleOf(::MediaStorePhotoRepository) { bind<PhotoRepository>() }
    singleOf(::ScanResultsRepository)

    // Other
    single<DataStore<Preferences>> {
        val settingsFile = get<Context>().filesDir.resolve("settings.preferences_pb")
        createSettingsDataStore(settingsFile.absolutePath)
    }
}
