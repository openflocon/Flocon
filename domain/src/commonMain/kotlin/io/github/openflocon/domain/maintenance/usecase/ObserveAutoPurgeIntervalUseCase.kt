package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import kotlinx.coroutines.flow.Flow

class ObserveAutoPurgeIntervalUseCase(
    private val repository: DatabaseMaintenanceRepository,
) {
    operator fun invoke(): Flow<AutoPurgeInterval> = repository.autoPurgeInterval
}
