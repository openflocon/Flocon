package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.models.maintenance.PurgeInterval

/** Deletes the saved screenshots and video recordings older than [PurgeInterval], on demand. */
class CleanMediaFilesUseCase(
    private val repository: MediaFilesRepository,
) {
    suspend operator fun invoke(
        interval: PurgeInterval,
        nowMs: Long = System.currentTimeMillis(),
    ) {
        val before = if (interval == PurgeInterval.Everything) {
            Long.MAX_VALUE
        } else {
            nowMs - interval.days * DAY_MS
        }
        repository.deleteFilesBefore(before)
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
