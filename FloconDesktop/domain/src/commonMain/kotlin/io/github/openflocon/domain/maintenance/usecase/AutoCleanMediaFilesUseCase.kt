package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import kotlinx.coroutines.flow.first

/** Called when the app starts: deletes the screenshots and recordings older than the interval chosen in the settings, if any. */
class AutoCleanMediaFilesUseCase(
    private val repository: MediaFilesRepository,
) {
    suspend operator fun invoke(nowMs: Long = System.currentTimeMillis()) {
        val interval = repository.autoCleanupInterval.first()
        if (interval == AutoPurgeInterval.Never) return
        repository.deleteFilesBefore(nowMs - interval.days * DAY_MS)
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
