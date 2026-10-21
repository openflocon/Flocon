package io.github.openflocon.domain.versions.repository

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import kotlinx.coroutines.flow.Flow

interface VersionsCheckerRepository {
    val lastVersion: Flow<String?>
    suspend fun checkIsLastVersion(): Either<Throwable, String>
    suspend fun getReleaseNotes(): Either<Throwable, List<ReleaseNoteDomainModel>>
}
