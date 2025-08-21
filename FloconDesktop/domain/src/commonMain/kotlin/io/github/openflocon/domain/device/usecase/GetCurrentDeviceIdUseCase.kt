package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DevicesRepository

class GetCurrentDeviceIdUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(): DeviceId? = devicesRepository.getCurrentDeviceId()
}
