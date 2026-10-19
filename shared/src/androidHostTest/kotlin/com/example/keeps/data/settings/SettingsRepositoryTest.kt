package com.example.keeps.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import app.cash.turbine.test
import com.example.keeps.fakes.FakeDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class SettingsRepositoryTest {

    private lateinit var fakeDataStore: FakeDataStore

    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setUp() {
        fakeDataStore = FakeDataStore()

        settingsRepository = SettingsRepository(fakeDataStore)
    }

    @Test
    fun `WHEN similarity threshold, GIVEN value not set, THEN emits default`() = runTest {
        assertEquals(
            SettingsRepository.DEFAULT_SIMILARITY_THRESHOLD,
            settingsRepository.similarityThreshold.first(),
        )
    }

    @Test
    fun `WHEN set similarity threshold, THEN emits updates`() = runTest {
        settingsRepository.similarityThreshold.test {
            assertEquals(SettingsRepository.DEFAULT_SIMILARITY_THRESHOLD, awaitItem())

            settingsRepository.setSimilarityThreshold(0.25f)
            assertEquals(0.25f, awaitItem())

            settingsRepository.setSimilarityThreshold(0.75f)
            assertEquals(0.75f, awaitItem())
        }
    }

    @Test
    fun `WHEN set threshold GIVEN valid input, THEN updates threshold`() = runTest {
        settingsRepository.setSimilarityThreshold(0.55f)

        assertEquals(0.55f, settingsRepository.similarityThreshold.first())
    }

    @Test
    fun `WHEN set threshold GIVEN value above range, THEN clamps threshold`() = runTest {
        settingsRepository.setSimilarityThreshold(1.5f)

        assertEquals(1f, settingsRepository.similarityThreshold.first())
    }

    @Test
    fun `WHEN set threshold GIVEN value below range, THEN clamps threshold`() = runTest {
        settingsRepository.setSimilarityThreshold(-0.5f)

        assertEquals(0f, settingsRepository.similarityThreshold.first())
    }
}
