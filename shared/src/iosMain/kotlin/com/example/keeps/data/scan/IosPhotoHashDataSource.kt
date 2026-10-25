package com.example.keeps.data.scan

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.keeps.domain.scan.PhotoHashDataSource
import com.example.keeps.db.KeepsDatabase

fun createIosPhotoHashDataSource(): PhotoHashDataSource =
    SqlDelightPhotoHashDataSource(NativeSqliteDriver(KeepsDatabase.Schema, "keeps.db"))
