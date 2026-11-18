package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval

class SetAutoPurgeIntervalUseCase(
    private val repository: DatabaseMaintenanceRepository,
) {
    suspend operator fun invoke(interval: AutoPurgeInterval) {
        repository.setAutoPurgeInterval(interval)
    }
}
