package com.example.keeps.domain.di

import com.example.keeps.domain.usecase.CreateDeleteRequestUseCase
import com.example.keeps.domain.usecase.GroupPhotosUseCase
import com.example.keeps.domain.usecase.SimilarityGroupPhotosUseCase
import com.example.keeps.domain.usecase.StartPhotoScanUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    singleOf(::SimilarityGroupPhotosUseCase) bind GroupPhotosUseCase::class
    singleOf(::CreateDeleteRequestUseCase)
    singleOf(::StartPhotoScanUseCase)
}
