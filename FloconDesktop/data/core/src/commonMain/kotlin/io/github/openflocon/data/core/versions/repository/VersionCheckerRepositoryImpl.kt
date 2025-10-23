package io.github.openflocon.data.core.versions.repository

import io.github.openflocon.data.core.versions.datasource.VersionCheckerDatasource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

internal class VersionCheckerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val versionCheckerDatasource: VersionCheckerDatasource,
) : VersionsCheckerRepository {

    private val _lastVersion = MutableStateFlow<String?>(null)
    override val lastVersion = _lastVersion.asStateFlow()

    override suspend fun checkIsLastVersion(): Either<Throwable, String> {
        return withContext(dispatcherProvider.data) {
            versionCheckerDatasource.fetchLatestVersion()
                .alsoSuccess {
                    _lastVersion.value = it
                }
        }
    }

}
