package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

class GetCurrentDeviceIdAndPackageNameUseCase(
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentDeviceAppUseCase: GetCurrentDeviceAppUseCase,
) {
    suspend operator fun invoke(): DeviceIdAndPackageNameDomainModel? {
        val deviceId = getCurrentDeviceIdUseCase() ?: return null
        val packageName = getCurrentDeviceAppUseCase()?.packageName ?: return null

        return DeviceIdAndPackageNameDomainModel(
            deviceId = deviceId,
            packageName = packageName,
        )
    }
}
