package com.example.keeps.data.settings

import app.cash.turbine.test
import com.example.keeps.fakes.FakeDataStore
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
            settingsRepository.getSimilarityThreshold(),
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

        assertEquals(0.55f, settingsRepository.getSimilarityThreshold())
    }

    @Test
    fun `WHEN set threshold GIVEN value above range, THEN clamps threshold`() = runTest {
        settingsRepository.setSimilarityThreshold(1.5f)

        assertEquals(1f, settingsRepository.getSimilarityThreshold())
    }

    @Test
    fun `WHEN set threshold GIVEN value below range, THEN clamps threshold`() = runTest {
        settingsRepository.setSimilarityThreshold(-0.5f)

        assertEquals(0f, settingsRepository.getSimilarityThreshold())
    }
}
