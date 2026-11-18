package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository

class GetReleaseNotesUseCase(
    private val versionsCheckerRepository: VersionsCheckerRepository,
) {
    suspend operator fun invoke(): Either<Throwable, List<ReleaseNoteDomainModel>> = versionsCheckerRepository.getReleaseNotes()
        .mapSuccess { notes ->
            notes.sortedWith { a, b ->
                when {
                    isRemoteVersionNewer(localVersion = b.version, remoteVersion = a.version) -> -1
                    isRemoteVersionNewer(localVersion = a.version, remoteVersion = b.version) -> 1
                    else -> 0
                }
            }
        }
}
