package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.firstOrNull

class DeleteDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(deviceId: DeviceId) {
        devicesRepository.deleteDevice(deviceId)
    }
}
