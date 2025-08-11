package io.github.openflocon.domain.settings

import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.common.Failure
import io.github.openflocon.flocondesktop.common.Success
import io.github.openflocon.flocondesktop.common.findAdbPath
import io.github.openflocon.domain.settings.repository.SettingsRepository

class InitAdbPathUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): Either<Throwable, Unit> {
        val adbPathFromSettings = settingsRepository.getAdbPath()
        return if (adbPathFromSettings == null) {
            val foundAdbPath = findAdbPath()
            if (foundAdbPath != null) {
                settingsRepository.setAdbPath(foundAdbPath)
                Success(Unit)
            } else {
                Failure(Throwable("adb not found"))
            }
        } else {
            Success(Unit)
        }
    }
}
