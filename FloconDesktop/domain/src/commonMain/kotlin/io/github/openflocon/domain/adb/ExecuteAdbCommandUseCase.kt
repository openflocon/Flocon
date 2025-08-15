package io.github.openflocon.domain.adb

import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.settings.repository.SettingsRepository

class ExecuteAdbCommandUseCase(
    private val adbRepository: AdbRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(
        target: AdbCommandTargetDomainModel,
        command: String,
    ): Either<Throwable, String> {
        val adbPath = settingsRepository.getAdbPath() ?: return Failure(Throwable("No adb path"))

        val deviceSerial = when (target) {
            is AdbCommandTargetDomainModel.Device -> adbRepository.getAdbSerial(target.deviceId)
            is AdbCommandTargetDomainModel.AllDevices -> null
        }
        return adbRepository.executeAdbCommand(
            adbPath = adbPath,
            deviceSerial = deviceSerial,
            command = command,
        )
    }
}
