package com.example.keeps.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.keeps.data.media.DHashPhotoHasher
import com.example.keeps.data.media.MediaStorePhotoRepository
import com.example.keeps.data.media.PhotoDataSource
import com.example.keeps.data.media.PhotoHashRepositoryImpl
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.data.scan.SqlDelightPhotoHashDataSource
import com.example.keeps.data.settings.SettingsRepository
import com.example.keeps.data.settings.createSettingsDataStore
import com.example.keeps.db.KeepsDatabase
import com.example.keeps.domain.repository.PhotoHashRepository
import com.example.keeps.domain.repository.PhotoRepository
import com.example.keeps.domain.scan.PhotoHashDataSource
import com.example.keeps.domain.scan.PhotoHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val DATABASE_NAME = "keeps.db"

val dataModule = module {
    // Data Source
    single<PhotoDataSource> {
        PhotoDataSource(
            contentResolver = get<Context>().contentResolver,
        )
    }

    single<PhotoHashDataSource> {
        val driver = AndroidSqliteDriver(KeepsDatabase.Schema, get<Context>(), DATABASE_NAME)
        SqlDelightPhotoHashDataSource(driver)
    }

    single<PhotoHasher> {
        DHashPhotoHasher(
            contentResolver = get<Context>().contentResolver,
        )
    }

    // Repository
    singleOf(::PhotoHashRepositoryImpl) bind PhotoHashRepository::class
    singleOf(::MediaStorePhotoRepository) bind PhotoRepository::class
    singleOf(::ScanResultsRepository)
    singleOf(::SettingsRepository)

    // Other
    single<DataStore<Preferences>> {
        val settingsFile = get<Context>().filesDir.resolve("settings.preferences_pb")
        createSettingsDataStore(settingsFile.absolutePath)
    }

    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
