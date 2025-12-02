package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository

class CheckIsDesktopOnLastVersionUseCase(
    private val versionsCheckerRepository: VersionsCheckerRepository,
) {
    suspend operator fun invoke(current: String): Either<Throwable, IsLastVersionDomainModel> = versionsCheckerRepository.checkIsLastVersion()
        .mapSuccess { lastVersion ->
            val isLastVersion = isRemoteVersionNewer(localVersion = current, remoteVersion = lastVersion)
            when (isLastVersion) {
                true -> IsLastVersionDomainModel.NewVersionAvailable(
                    name = lastVersion,
                    link = "https://github.com/openflocon/Flocon/releases/tag/$lastVersion",
                    oldVersion = current,
                )
                false -> IsLastVersionDomainModel.RunningLastVersion
            }
        }
}

fun isRemoteVersionNewer(localVersion: String, remoteVersion: String): Boolean {
    val localParts = localVersion.split(".").mapNotNull { it.toIntOrNull() }
    val remoteParts = remoteVersion.split(".").mapNotNull { it.toIntOrNull() }

    val maxLength = maxOf(localParts.size, remoteParts.size)
    for (i in 0 until maxLength) {
        val local = localParts.getOrElse(i) { 0 }
        val remote = remoteParts.getOrElse(i) { 0 }
        if (remote > local) return true
        if (remote < local) return false
    }
    return false
}
