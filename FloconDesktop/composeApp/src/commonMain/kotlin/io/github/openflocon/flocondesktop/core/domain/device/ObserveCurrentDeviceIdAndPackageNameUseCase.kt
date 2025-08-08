package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveCurrentDeviceIdAndPackageNameUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceIdAndPackageNameDomainModel?> = combine(
        devicesRepository.currentDevice,
        devicesRepository.currentDeviceApp
    ) { device, app ->
        if (device != null && app != null) {
            DeviceIdAndPackageNameDomainModel(deviceId = device.deviceId, packageName = app.packageName)
        } else {
            null
        }
    }
        .distinctUntilChanged()
}
