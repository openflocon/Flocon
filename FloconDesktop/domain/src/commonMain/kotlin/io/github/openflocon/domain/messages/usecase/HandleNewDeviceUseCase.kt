package io.github.openflocon.domain.messages.usecase

import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.settings.repository.SettingsRepository

class HandleNewDeviceUseCase(
    private val adbRepository: AdbRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        // check if we have a match for serial + app/package
        val serial = adbRepository.getAdbSerial(deviceIdAndPackageName.deviceId)
        if (serial == null) {
            val adbPath = settingsRepository.getAdbPath() ?: return
            // if not send a message through adb
            val serialVariableName = "FLOCON_SERIAL"
            adbRepository.executeAdbAskSerialToAllDevices(
                adbPath = adbPath,
                command = buildString {
                    append("shell am broadcast")
                    append(" ")
                    append("-a \"io.github.openflocon.flocon.DETECT\"")
                    append(" ")
                    append("-n \"${deviceIdAndPackageName.packageName}/io.github.openflocon.flocon.FloconBroadcastReceiver\"")
                    append(" --es \"serial\" \"$serialVariableName\"")
                },
                serialVariableName = serialVariableName,
            )
        }
    }
}
