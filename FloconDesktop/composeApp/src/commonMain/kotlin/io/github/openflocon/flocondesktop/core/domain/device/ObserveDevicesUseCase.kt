package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository

class ObserveDevicesUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke() = devicesRepository.devices
}
