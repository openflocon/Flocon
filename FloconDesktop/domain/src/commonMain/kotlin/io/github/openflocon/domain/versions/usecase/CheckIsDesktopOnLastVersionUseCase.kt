package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository

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

fun isRemoteVersionNewer(localVersion: String, remoteVersion: String): Boolean {
    val localParts = localVersion.toVersionParts()
    val remoteParts = remoteVersion.toVersionParts()

    // an unknown version (empty, "unknown"...) can't be compared: never claim an update
    if (localParts.isEmpty() || remoteParts.isEmpty()) return false

    val maxLength = maxOf(localParts.size, remoteParts.size)
    for (i in 0 until maxLength) {
        val local = localParts.getOrElse(i) { 0 }
        val remote = remoteParts.getOrElse(i) { 0 }
        if (remote > local) return true
        if (remote < local) return false
    }
    return false
}

// "v1.10.0-SNAPSHOT" -> [1, 10, 0]: keeps the leading digits of each segment, stops at the first non numeric one
private fun String.toVersionParts(): List<Int> = removePrefix("v")
    .split(".")
    .map { segment -> segment.takeWhile { it.isDigit() } }
    .takeWhile { it.isNotEmpty() }
    .map { it.toInt() }
