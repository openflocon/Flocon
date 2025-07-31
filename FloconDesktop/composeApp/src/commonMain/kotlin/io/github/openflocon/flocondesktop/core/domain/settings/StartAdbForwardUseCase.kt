package io.github.openflocon.flocondesktop.core.domain.settings

import io.github.openflocon.flocondesktop.SERVER_PORT
import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.common.Failure
import io.github.openflocon.flocondesktop.common.executeAdbCommand
import io.github.openflocon.flocondesktop.core.domain.settings.repository.SettingsRepository

class StartAdbForwardUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Either<Throwable, Unit> {
        val adbPath = settingsRepository.getAdbPath()
        return if (adbPath != null) {
            executeAdbCommand(adbPath = adbPath, command = "reverse tcp:$SERVER_PORT tcp:$SERVER_PORT")
                .mapSuccess { Unit }
        } else {
            Failure(Throwable("adb path is empty"))
        }
    }
}
