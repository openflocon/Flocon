package io.github.openflocon.data.core.versions.datasource

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel

interface VersionCheckerDatasource {
    suspend fun fetchLatestVersion(): Either<Throwable, String>
    suspend fun fetchReleases(): Either<Throwable, List<ReleaseNoteDomainModel>>
}
