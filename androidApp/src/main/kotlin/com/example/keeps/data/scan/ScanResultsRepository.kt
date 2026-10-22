package com.example.keeps.data.scan

import com.example.keeps.domain.model.PhotoGroup
import com.example.keeps.presentation.home.HomeViewModel
import com.example.keeps.presentation.results.ResultsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory, app-scoped (Koin `single`) store for the latest scan's grouped
 * results and threshold, bridging [HomeViewModel] (which writes results) and
 * [ResultsViewModel] (which reads them) — these are separate nav-entry-scoped
 * ViewModels with no other way to share state.
 *
 * [setResults] always fully **replaces** the previous scan's groups: a new
 * scan overrides the old one. Whether a future scan should instead skip
 * already-processed photos or merge with prior results is an open product
 * decision, not implemented here.
 */
class ScanResultsRepository {

    private val _results = MutableStateFlow(ScanResults.EMPTY)
    val results: StateFlow<ScanResults> = _results.asStateFlow()

    fun setResults(groups: List<PhotoGroup>, similarityThreshold: Float) {
        _results.value = ScanResults(groups, similarityThreshold)
    }
}
