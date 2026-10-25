package com.example.keeps.data.scan

import com.example.keeps.domain.model.PhotoGroup
import com.example.keeps.domain.model.ScanProgress
import com.example.keeps.domain.model.ScanResults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * App-scoped, in-memory store for observable scan results and progress.
 * Background analysis publishes partial groups here independently of screen lifetimes.
 *
 * A new scan fully **replaces** the previous one ([initializeScan] clears the
 * groups). Whether a future scan should skip previously analyzed photos or merge
 * with prior results is an open product decision, not implemented here.
 */
class ScanResultsRepository {

    private val _results = MutableStateFlow(ScanResults.EMPTY)
    val results: StateFlow<ScanResults> = _results.asStateFlow()

    private val _progress = MutableStateFlow(ScanProgress())
    val progress: StateFlow<ScanProgress> = _progress.asStateFlow()

    fun setResults(groups: List<PhotoGroup>, similarityThreshold: Float) {
        _results.update { ScanResults(groups, similarityThreshold) }
    }

    fun initializeScan(totalPhotoCount: Int, similarityThreshold: Float) {
        _results.update { ScanResults(emptyList(), similarityThreshold) }
        _progress.update {
            ScanProgress(
                analyzedPhotoCount = 0,
                totalPhotoCount = totalPhotoCount,
                isComplete = false,
            )
        }
    }

    fun updateScan(analyzedPhotoCount: Int, groups: List<PhotoGroup>) {
        _results.update { it.copy(groups = groups) }
        _progress.update { it.copy(analyzedPhotoCount = analyzedPhotoCount) }
    }

    fun completeScan() {
        _progress.update { it.copy(analyzedPhotoCount = it.totalPhotoCount, isComplete = true) }
    }
}
