package io.github.openflocon.data.core.versions.datasource

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel

interface VersionCheckerDatasource {
    /** The version of the latest release of the GitHub [repository] (`owner/name`). */
    suspend fun fetchLatestVersion(repository: String): Either<Throwable, String>
    suspend fun fetchReleases(): Either<Throwable, List<ReleaseNoteDomainModel>>
}
