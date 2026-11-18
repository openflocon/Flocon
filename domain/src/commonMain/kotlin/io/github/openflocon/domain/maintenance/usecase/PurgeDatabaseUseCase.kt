package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository
import io.github.openflocon.domain.models.maintenance.PurgeInterval

class PurgeDatabaseUseCase(
    private val repository: DatabaseMaintenanceRepository,
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
        repository.purgeDataBefore(before)
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
