package com.example.keeps.presentation.di

import com.example.keeps.presentation.home.HomeViewModel
import com.example.keeps.presentation.results.ResultsViewModel
import com.example.keeps.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::ResultsViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
}
