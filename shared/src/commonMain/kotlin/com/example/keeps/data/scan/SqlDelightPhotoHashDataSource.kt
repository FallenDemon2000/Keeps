package com.example.keeps.data.scan

import app.cash.sqldelight.db.SqlDriver
import com.example.keeps.db.KeepsDatabase
import com.example.keeps.domain.scan.PhotoHashDataSource

/** SQLDelight storage for disposable, recomputable photo hashes. */
class SqlDelightPhotoHashDataSource(driver: SqlDriver) : PhotoHashDataSource {

    private val queries = KeepsDatabase(driver).photoHashQueries

    override fun get(photoId: String, sizeBytes: Long): Long? =
        queries.selectOne(photoId).executeAsOneOrNull()
            ?.takeIf { it.sizeBytes == sizeBytes }
            ?.hash

    override fun put(photoId: String, sizeBytes: Long, hash: Long) {
        queries.upsert(photoId, sizeBytes, hash)
    }
}
