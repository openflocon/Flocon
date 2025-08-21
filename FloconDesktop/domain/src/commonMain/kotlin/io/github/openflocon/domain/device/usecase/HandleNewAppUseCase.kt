package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository

class HandleNewAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        if(!devicesRepository.hasAppIcon(
            deviceId = deviceIdAndPackageName.deviceId,
            appPackageName = deviceIdAndPackageName.packageName
        )) {
            devicesRepository.askForDeviceAppIcon(deviceIdAndPackageName)
        }
    }
}
