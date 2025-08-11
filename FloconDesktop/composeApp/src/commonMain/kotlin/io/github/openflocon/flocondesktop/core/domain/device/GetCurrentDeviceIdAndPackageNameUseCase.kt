package io.github.openflocon.flocondesktop.core.domain.device

import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel

class GetCurrentDeviceIdAndPackageNameUseCase(
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
    private val getCurrentDeviceAppUseCase: GetCurrentDeviceAppUseCase,
) {
    operator fun invoke(): DeviceIdAndPackageNameDomainModel? {
        val deviceId = getCurrentDeviceUseCase()?.deviceId ?: return null
        val packageName = getCurrentDeviceAppUseCase()?.packageName ?: return null

        return DeviceIdAndPackageNameDomainModel(
            deviceId = deviceId,
            packageName = packageName,
        )
    }
}
