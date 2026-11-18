package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository

class GetReleaseNoteUseCase(
    private val versionsCheckerRepository: VersionsCheckerRepository,
) {
    suspend operator fun invoke(version: String): Either<Throwable, ReleaseNoteDomainModel?> {
        val wanted = version.removePrefix("v")
        return versionsCheckerRepository.getReleaseNotes()
            .mapSuccess { notes -> notes.firstOrNull { it.version.removePrefix("v") == wanted } }
    }
}
