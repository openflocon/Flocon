package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow

class ObserveDevicesUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<List<DeviceDomainModel>> = devicesRepository.devices
}
