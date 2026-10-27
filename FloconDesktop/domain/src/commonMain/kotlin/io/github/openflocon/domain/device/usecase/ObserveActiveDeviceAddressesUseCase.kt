package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow

class ObserveActiveDeviceAddressesUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<Map<DeviceId, String>> = devicesRepository.activeAddresses
}
