package com.florent37.flocondesktop.core.domain.settings

import com.florent37.flocondesktop.common.findAdbPath
import com.florent37.flocondesktop.core.domain.settings.repository.SettingsRepository

class InitAdbPathUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() {
        val adbPathFromSettings = settingsRepository.getAdbPath()
        if (adbPathFromSettings == null) {
            val foundAdbPath = findAdbPath()
            if (foundAdbPath != null) {
                settingsRepository.setAdbPath(foundAdbPath)
            }
        }
    }
}
