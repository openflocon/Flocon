package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.models.DeviceDomainModel
import kotlinx.coroutines.flow.Flow

class ObserveCurrentDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceDomainModel?> = devicesRepository.currentDevice
}
