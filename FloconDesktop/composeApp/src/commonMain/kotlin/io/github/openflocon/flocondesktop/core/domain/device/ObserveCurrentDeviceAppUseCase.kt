package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.models.DeviceAppDomainModel
import kotlinx.coroutines.flow.Flow

class ObserveCurrentDeviceAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceAppDomainModel?> = devicesRepository.currentDeviceApp
}
