package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.adb.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.settings.repository.SettingsRepository

class StartAdbForwardUseCase(
    private val settingsRepository: SettingsRepository,
    private val adbRepository: AdbRepository
) {
    operator fun invoke(): Either<Throwable, Unit> {
        val adbPath = settingsRepository.getAdbPath()
        return if (adbPath != null) {
            adbRepository.executeAdbCommand(
                adbPath = adbPath,
                command = "reverse tcp:${Constant.SERVER_PORT} tcp:${Constant.SERVER_PORT}",
                target = AdbCommandTargetDomainModel.AllDevices,
            ).mapSuccess { Unit }
        } else {
            Failure(Throwable("adb path is empty"))
        }
    }
}
