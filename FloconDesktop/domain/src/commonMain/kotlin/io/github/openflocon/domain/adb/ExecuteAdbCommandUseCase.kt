package io.github.openflocon.domain.adb

import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.device.models.supportsAdb
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class ExecuteAdbCommandUseCase(
    private val adbRepository: AdbRepository,
    private val settingsRepository: SettingsRepository,
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(
        target: AdbCommandTargetDomainModel,
        command: String,
    ): Either<Throwable, String> {
        if (target is AdbCommandTargetDomainModel.Device) {
            val device = devicesRepository.devices.first().firstOrNull { it.deviceId == target.deviceId }
            if (device != null && !device.supportsAdb()) {
                return Failure(Throwable(ADB_NOT_AVAILABLE_OVER_WIFI))
            }
        }

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

const val ADB_NOT_AVAILABLE_OVER_WIFI = "ADB is not available for devices connected over wifi"
