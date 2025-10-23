package io.github.openflocon.data.core.versions.datasource

import io.github.openflocon.domain.common.Either

interface VersionCheckerDatasource {
    suspend fun fetchLatestVersion(): Either<Throwable, String>
}
