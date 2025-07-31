package io.github.openflocon.flocondesktop.core.domain.settings

import com.florent37.flocondesktop.SERVER_PORT
import com.florent37.flocondesktop.common.Either
import com.florent37.flocondesktop.common.Failure
import com.florent37.flocondesktop.common.executeAdbCommand
import com.florent37.flocondesktop.core.domain.settings.repository.SettingsRepository

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
