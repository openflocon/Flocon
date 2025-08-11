package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveCurrentDeviceIdAndPackageNameUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceIdAndPackageNameDomainModel?> = combine(
        devicesRepository.currentDevice,
        devicesRepository.currentDeviceApp,
    ) { device, app ->
        if (device != null && app != null) {
            DeviceIdAndPackageNameDomainModel(deviceId = device.deviceId, packageName = app.packageName)
        } else {
            null
        }
    }
        .distinctUntilChanged()
}
