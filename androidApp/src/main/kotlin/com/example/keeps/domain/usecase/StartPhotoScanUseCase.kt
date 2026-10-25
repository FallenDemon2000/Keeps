package com.example.keeps.domain.usecase

import android.net.Uri
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.ktx.runCancellable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/**
 * App-scoped owner of the running scan, so analysis continues while the user
 * moves from Home to Results (a ViewModel scope would be cancelled). Progress
 * and partial groups are published to [ScanResultsRepository]. Starting a new
 * scan cancels the previous one. Invocation returns after scheduling analysis,
 * not after its completion.
 */
class StartPhotoScanUseCase(
    private val scope: CoroutineScope,
    private val groupPhotos: GroupPhotosUseCase,
    private val scanResultsRepository: ScanResultsRepository,
) {

    private var job: Job? = null

    @Synchronized
    operator fun invoke(uris: List<Uri>, similarityThreshold: Float) {
        job?.cancel()

        scanResultsRepository.initializeScan(uris.size, similarityThreshold)
        job = scope.launch {
            runCancellable {
                groupPhotos(uris, similarityThreshold).collect { update ->
                    currentCoroutineContext().ensureActive()
                    scanResultsRepository.updateScan(update.analyzedPhotoCount, update.groups)
                }
            }
            scanResultsRepository.completeScan()
        }
    }
}
