package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import kotlinx.coroutines.flow.Flow

class ObserveAutoMediaCleanupIntervalUseCase(
    private val repository: MediaFilesRepository,
) {
    operator fun invoke(): Flow<AutoPurgeInterval> = repository.autoCleanupInterval
}
