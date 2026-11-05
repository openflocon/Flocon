package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval

class SetAutoMediaCleanupIntervalUseCase(
    private val repository: MediaFilesRepository,
) {
    suspend operator fun invoke(interval: AutoPurgeInterval) {
        repository.setAutoCleanupInterval(interval)
    }
}
