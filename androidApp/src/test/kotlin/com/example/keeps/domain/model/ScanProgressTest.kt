package com.example.keeps.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScanProgressTest {

    @Test
    fun largeScanReachesFirstResultsAtOneQuarter() {
        val beforeGate = ScanProgress(24, 100, false)
        val atGate = beforeGate.copy(analyzedPhotoCount = 25)

        assertEquals(25, beforeGate.photosNeededForFirstResults)
        assertFalse(beforeGate.isReadyForFirstResults)
        assertTrue(atGate.isReadyForFirstResults)
        assertFalse(atGate.isComplete)
        assertEquals(0.25f, atGate.fraction, 0f)
        assertEquals(1f, atGate.loadingFraction, 0f)
    }

    @Test
    fun smallScanWaitsForEightOrAllSelectedPhotos() {
        assertFalse(ScanProgress(7, 20, false).isReadyForFirstResults)
        assertTrue(ScanProgress(8, 20, false).isReadyForFirstResults)
        assertFalse(ScanProgress(3, 4, false).isReadyForFirstResults)
        assertTrue(ScanProgress(4, 4, false).isReadyForFirstResults)
    }

    @Test
    fun quarterGateRoundsUp() {
        assertFalse(ScanProgress(25, 101, false).isReadyForFirstResults)
        assertTrue(ScanProgress(26, 101, false).isReadyForFirstResults)
    }

    @Test
    fun emptyAndCompletedScansAreReady() {
        assertTrue(ScanProgress().isReadyForFirstResults)
        assertEquals(1f, ScanProgress().fraction, 0f)
        assertEquals(1f, ScanProgress().loadingFraction, 0f)
        assertTrue(ScanProgress(1, 100, true).isReadyForFirstResults)
    }
}
