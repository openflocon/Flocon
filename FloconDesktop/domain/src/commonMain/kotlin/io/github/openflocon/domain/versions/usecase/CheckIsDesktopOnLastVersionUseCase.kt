package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import io.github.z4kn4fein.semver.toVersionOrNull

class CheckIsDesktopOnLastVersionUseCase(
    private val versionsCheckerRepository: VersionsCheckerRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(current: String): Either<Throwable, IsLastVersionDomainModel> = versionsCheckerRepository.checkIsLastVersion()
        .mapSuccess { lastVersion ->
            val isNewerThanCurrent = isRemoteVersionNewer(localVersion = current, remoteVersion = lastVersion)
            val dismissedVersion = settingsRepository.getDismissedDesktopVersion()
            val isNewerThanDismissed = dismissedVersion == null || isRemoteVersionNewer(localVersion = dismissedVersion, remoteVersion = lastVersion)

            if (isNewerThanCurrent && isNewerThanDismissed) {
                IsLastVersionDomainModel.NewVersionAvailable(
                    name = lastVersion,
                    link = "https://github.com/openflocon/Flocon/releases/tag/$lastVersion",
                    oldVersion = current,
                )
            } else {
                IsLastVersionDomainModel.RunningLastVersion
            }
        }
}

/** Semver comparison ("v1.10.0", "1.10" are accepted). An unknown version (empty, "unknown"...) can't be compared: never claims an update. */
fun isRemoteVersionNewer(localVersion: String, remoteVersion: String): Boolean {
    val local = localVersion.toVersionOrNull(strict = false) ?: return false
    val remote = remoteVersion.toVersionOrNull(strict = false) ?: return false
    return remote > local
}
