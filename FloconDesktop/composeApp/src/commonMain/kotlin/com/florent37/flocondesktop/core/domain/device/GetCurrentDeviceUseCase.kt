package com.florent37.flocondesktop.core.domain.device

import com.florent37.flocondesktop.core.domain.device.repository.DevicesRepository
import com.florent37.flocondesktop.messages.domain.model.DeviceDomainModel

class GetCurrentDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): DeviceDomainModel? = devicesRepository.getCurrentDevice()
}
