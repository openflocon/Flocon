package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository

class GetCurrentDeviceIdAndPackageNameUseCase(
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(): DeviceIdAndPackageNameDomainModel? {
        val deviceId = getCurrentDeviceIdUseCase() ?: return null
        val packageName = devicesRepository.getDeviceSelectedApp(deviceId)?.packageName ?: return null

        return DeviceIdAndPackageNameDomainModel(
            deviceId = deviceId,
            packageName = packageName,
        )
    }
}
