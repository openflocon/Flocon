package com.florent37.flocondesktop.core.domain.device

import com.florent37.flocondesktop.core.domain.device.repository.DevicesRepository

class ObserveDevicesUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke() = devicesRepository.devices
}
