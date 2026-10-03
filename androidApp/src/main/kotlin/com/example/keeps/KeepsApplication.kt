package com.example.keeps

import android.app.Application
import com.example.keeps.data.di.dataModule
import com.example.keeps.domain.di.domainModule
import com.example.keeps.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KeepsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KeepsApplication)
            modules(dataModule, domainModule, presentationModule)
        }
    }
}
