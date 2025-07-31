package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel
import kotlinx.coroutines.flow.Flow

class ObserveCurrentDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceDomainModel?> = devicesRepository.currentDevice
}
