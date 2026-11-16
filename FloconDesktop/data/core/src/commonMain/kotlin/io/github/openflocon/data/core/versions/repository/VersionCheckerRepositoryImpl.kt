package io.github.openflocon.data.core.versions.repository

import io.github.openflocon.data.core.versions.datasource.VersionCheckerDatasource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.versions.model.FloconSdk
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

internal class VersionCheckerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val versionCheckerDatasource: VersionCheckerDatasource,
) : VersionsCheckerRepository {

    private val _lastDesktopVersion = MutableStateFlow<String?>(null)
    override val lastDesktopVersion = _lastDesktopVersion.asStateFlow()

    override suspend fun checkIsLastVersion(): Either<Throwable, String> = withContext(dispatcherProvider.data) {
        versionCheckerDatasource.fetchLatestVersion(repository = DESKTOP_REPOSITORY)
            .alsoSuccess { version ->
                _lastDesktopVersion.value = version
                // the desktop app and the Kotlin SDK are released from the same repository
                sdkVersions.update { it + (FloconSdk.Kotlin to version) }
            }
    }

    private val sdkVersions = MutableStateFlow<Map<FloconSdk, String>>(emptyMap())
    private val sdkVersionsMutex = Mutex()
    private val sdkFetchFailures = mutableMapOf<FloconSdk, TimeMark>() // guarded by sdkVersionsMutex

    override fun observeLastClientSdkVersion(sdk: FloconSdk): Flow<String?> = sdkVersions
        .map { it[sdk] }
        .distinctUntilChanged()

    override suspend fun checkLastClientSdkVersion(sdk: FloconSdk) {
        withContext(dispatcherProvider.data) {
            // one call per SDK: GitHub limits the requests that are not authenticated
            sdkVersionsMutex.withLock {
                // an SDK we do not know has no repository to look at
                val repository = sdk.repository ?: return@withLock
                // after a failure (offline, rate limit) wait before asking GitHub again
                val recentlyFailed = sdkFetchFailures[sdk]?.let { it.elapsedNow() < RETRY_DELAY } ?: false
                if (sdk !in sdkVersions.value && !recentlyFailed) {
                    versionCheckerDatasource.fetchLatestVersion(repository = repository)
                        .alsoSuccess { version -> sdkVersions.update { it + (sdk to version) } }
                        .alsoFailure { sdkFetchFailures[sdk] = TimeSource.Monotonic.markNow() }
                }
            }
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

    private companion object {
        // the desktop app and the Kotlin SDK are released from the same repository
        val DESKTOP_REPOSITORY = checkNotNull(FloconSdk.Kotlin.repository)
        val RETRY_DELAY = 5.minutes
    }
}
