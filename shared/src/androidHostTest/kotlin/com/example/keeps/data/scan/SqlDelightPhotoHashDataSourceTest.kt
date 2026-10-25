package com.example.keeps.data.scan

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.keeps.db.KeepsDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SqlDelightPhotoHashDataSourceTest {

    @Test
    fun storesHashesAndInvalidatesChangedSizes() {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).use { driver ->
            KeepsDatabase.Schema.create(driver)
            val source = SqlDelightPhotoHashDataSource(driver)

            assertNull(source.get("photo", 10L))
            source.put("photo", 10L, -1L)
            assertEquals(-1L, source.get("photo", 10L))
            assertNull(source.get("photo", 11L))

            source.put("photo", 11L, 0L)
            assertEquals(0L, source.get("photo", 11L))
            assertNull(source.get("photo", 10L))
        }
    }
}
