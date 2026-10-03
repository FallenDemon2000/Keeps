package com.example.keeps.domain.di

import com.example.keeps.domain.usecase.GroupPhotosUseCase
import com.example.keeps.domain.usecase.RandomPhotoGroupingUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::RandomPhotoGroupingUseCase) { bind<GroupPhotosUseCase>() }
}
