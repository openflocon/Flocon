package com.florent37.flocondesktop.core.domain.settings

import com.florent37.flocondesktop.common.Either
import com.florent37.flocondesktop.common.Failure
import com.florent37.flocondesktop.common.executeSystemCommand
import com.florent37.flocondesktop.core.domain.settings.repository.SettingsRepository

class TestAdbUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Either<Throwable, Unit> {
        val adbPath = settingsRepository.getAdbPath()
        return if (adbPath != null) {
            executeSystemCommand("$adbPath start-server")
                .mapSuccess { Unit }
        } else {
            Failure(Throwable("adb path is empty"))
        }
    }
}
