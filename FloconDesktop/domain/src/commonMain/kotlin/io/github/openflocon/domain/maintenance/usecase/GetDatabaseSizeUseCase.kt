package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository

class GetDatabaseSizeUseCase(
    private val repository: DatabaseMaintenanceRepository,
) {
    suspend operator fun invoke(): Long = repository.getDatabaseSizeBytes()
}
