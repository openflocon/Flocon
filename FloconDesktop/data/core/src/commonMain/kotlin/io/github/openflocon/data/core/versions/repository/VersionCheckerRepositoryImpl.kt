package io.github.openflocon.data.core.versions.repository

import io.github.openflocon.data.core.versions.datasource.VersionCheckerDatasource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class VersionCheckerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val versionCheckerDatasource: VersionCheckerDatasource,
) : VersionsCheckerRepository {

    private val _lastVersion = MutableStateFlow<String?>(null)
    override val lastVersion = _lastVersion.asStateFlow()

    override suspend fun checkIsLastVersion(): Either<Throwable, String> = withContext(dispatcherProvider.data) {
        versionCheckerDatasource.fetchLatestVersion()
            .alsoSuccess {
                _lastVersion.value = it
            }
    }

    private val releaseNotesMutex = Mutex()
    private var releaseNotesCache: List<ReleaseNoteDomainModel>? = null

    override suspend fun getReleaseNotes(): Either<Throwable, List<ReleaseNoteDomainModel>> = withContext(dispatcherProvider.data) {
        releaseNotesMutex.withLock {
            releaseNotesCache?.success()
                ?: versionCheckerDatasource.fetchReleases()
                    .alsoSuccess { releaseNotesCache = it }
        }
    }
}
