package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel

class HandleDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(device: DeviceDomainModel): String {
        devicesRepository.register(device)
        return device.deviceId
    }
}
