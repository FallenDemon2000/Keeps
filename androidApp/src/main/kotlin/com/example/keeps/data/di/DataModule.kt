package com.example.keeps.data.di

import android.content.Context
import com.example.keeps.data.media.MediaStorePhotoRepository
import com.example.keeps.data.media.PhotoDataSource
import com.example.keeps.data.media.PhotoRepository
import com.example.keeps.data.scan.ScanResultsRepository
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
}
