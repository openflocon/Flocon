package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import kotlinx.coroutines.flow.Flow

class ObserveLastAvailableFloconVersionUseCase(
    private val versionsCheckerRepository: VersionsCheckerRepository,
) {
    operator fun invoke(): Flow<String?> = versionsCheckerRepository.lastVersion
}
