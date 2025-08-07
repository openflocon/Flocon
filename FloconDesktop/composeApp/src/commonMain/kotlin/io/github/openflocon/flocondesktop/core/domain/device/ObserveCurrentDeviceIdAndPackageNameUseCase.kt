package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveCurrentDeviceIdAndPackageNameUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceIdAndPackageName?> = combine(
        devicesRepository.currentDevice,
        devicesRepository.currentDeviceApp
    ) { device, app ->
        if (device != null && app != null) {
            DeviceIdAndPackageName(deviceId = device.deviceId, packageName = app.packageName)
        } else {
            null
        }
    }
}
