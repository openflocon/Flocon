package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository

class ObserveDevicesUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke() = devicesRepository.devices
}
