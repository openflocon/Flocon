package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import kotlinx.coroutines.flow.first

/** Called when the app starts: deletes the data older than the interval chosen in the settings, if any. */
class AutoPurgeDatabaseUseCase(
    private val repository: DatabaseMaintenanceRepository,
) {
    suspend operator fun invoke(nowMs: Long = System.currentTimeMillis()) {
        val interval = repository.autoPurgeInterval.first()
        if (interval == AutoPurgeInterval.Never) return
        repository.purgeDataBefore(nowMs - interval.days * DAY_MS)
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
