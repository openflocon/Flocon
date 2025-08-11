package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.settings.repository.SettingsRepository

class InitAdbPathUseCase(
    private val settingsRepository: SettingsRepository,
    private val adbRepository: AdbRepository
) {
    suspend operator fun invoke(): Either<Throwable, Unit> {
        val adbPathFromSettings = settingsRepository.getAdbPath()
        return if (adbPathFromSettings == null) {
            val foundAdbPath = adbRepository.findAdbPath()
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
