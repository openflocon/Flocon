package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel

class GetCurrentDeviceIdAndPackageNameUseCase(
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
    private val getCurrentDeviceAppUseCase: GetCurrentDeviceAppUseCase
) {
    operator fun invoke(): DeviceIdAndPackageNameDomainModel? {
        val deviceId = getCurrentDeviceUseCase()?.deviceId ?: return null
        val packageName = getCurrentDeviceAppUseCase()?.packageName ?: return null

        return DeviceIdAndPackageNameDomainModel(
            deviceId = deviceId,
            packageName = packageName
        )
    }
}
