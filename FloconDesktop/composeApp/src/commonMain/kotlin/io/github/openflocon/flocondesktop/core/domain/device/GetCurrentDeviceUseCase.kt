package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel

class GetCurrentDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): DeviceDomainModel? = devicesRepository.getCurrentDevice()
}
