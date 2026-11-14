package io.github.openflocon.domain.versions.repository

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.model.FloconSdk
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import kotlinx.coroutines.flow.Flow

interface VersionsCheckerRepository {
    /** The latest version of the desktop app. */
    val lastDesktopVersion: Flow<String?>
    suspend fun checkIsLastVersion(): Either<Throwable, String>

    /** The latest version of [sdk], null until [checkLastClientSdkVersion] succeeded. */
    fun observeLastClientSdkVersion(sdk: FloconSdk): Flow<String?>

    /** Fetches the latest version of [sdk] once: nothing is fetched when it is already known. */
    suspend fun checkLastClientSdkVersion(sdk: FloconSdk)

    suspend fun getReleaseNotes(): Either<Throwable, List<ReleaseNoteDomainModel>>
}
