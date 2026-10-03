package com.example.keeps.data.scan

import com.example.keeps.domain.model.PhotoGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory, app-scoped (Koin `single`) store for the latest scan's grouped
 * results, bridging [com.example.keeps.presentation.home.HomeViewModel] (which
 * writes results) and [com.example.keeps.presentation.results.ResultsViewModel]
 * (which reads them) — these are separate nav-entry-scoped ViewModels with no
 * other way to share state.
 *
 * [setResults] always fully **replaces** the previous scan's groups: a new
 * scan overrides the old one. Whether a future scan should instead skip
 * already-processed photos or merge with prior results is an open product
 * decision, not implemented here.
 */
class ScanResultsRepository {

    private val _groups = MutableStateFlow<List<PhotoGroup>>(emptyList())
    val groups: StateFlow<List<PhotoGroup>> = _groups.asStateFlow()

    fun setResults(groups: List<PhotoGroup>) {
        _groups.value = groups
    }
}
