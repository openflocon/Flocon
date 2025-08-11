package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import com.flocon.library.domain.models.DeviceAppDomainModel
import kotlinx.coroutines.flow.Flow

class ObserveCurrentDeviceAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceAppDomainModel?> = devicesRepository.currentDeviceApp
}
