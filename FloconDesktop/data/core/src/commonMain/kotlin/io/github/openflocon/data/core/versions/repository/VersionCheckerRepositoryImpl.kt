package io.github.openflocon.data.core.versions.repository

import io.github.openflocon.data.core.versions.datasource.VersionCheckerDatasource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import kotlinx.coroutines.withContext

internal class VersionCheckerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val versionCheckerDatasource: VersionCheckerDatasource,
) : VersionsCheckerRepository {
    override suspend fun checkIsLastVersion(): Either<Throwable, String> {
        return withContext(dispatcherProvider.data) {
            versionCheckerDatasource.fetchLatestVersion()
        }
    }

}
