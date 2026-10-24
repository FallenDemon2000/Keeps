package com.example.keeps.similarity

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.keeps.data.scan.HashCache
import com.example.keeps.data.scan.SqlDelightHashCache
import com.example.keeps.db.KeepsDatabase

fun createIosHashCache(): HashCache =
    SqlDelightHashCache(NativeSqliteDriver(KeepsDatabase.Schema, "keeps.db"))
