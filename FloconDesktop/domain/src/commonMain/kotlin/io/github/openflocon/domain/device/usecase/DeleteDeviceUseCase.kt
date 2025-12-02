package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DevicesRepository

class DeleteDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(deviceId: DeviceId) {
        devicesRepository.deleteDevice(deviceId)
    }
}
