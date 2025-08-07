package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName

class GetCurrentDeviceIdAndPackageNameUseCase(
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
    private val getCurrentDeviceAppUseCase: GetCurrentDeviceAppUseCase
) {
    operator fun invoke(): DeviceIdAndPackageName? {
        val deviceId = getCurrentDeviceUseCase()?.deviceId ?: return null
        val packageName = getCurrentDeviceAppUseCase()?.packageName ?: return null

        return DeviceIdAndPackageName(
            deviceId = deviceId,
            packageName = packageName
        )
    }
}
