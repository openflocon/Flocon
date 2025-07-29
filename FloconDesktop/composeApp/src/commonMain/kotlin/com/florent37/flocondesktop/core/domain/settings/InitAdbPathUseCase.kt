package com.florent37.flocondesktop.core.domain.settings

import com.florent37.flocondesktop.common.Either
import com.florent37.flocondesktop.common.Failure
import com.florent37.flocondesktop.common.Success
import com.florent37.flocondesktop.common.findAdbPath
import com.florent37.flocondesktop.core.domain.settings.repository.SettingsRepository

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
