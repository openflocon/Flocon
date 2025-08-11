package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import com.flocon.library.domain.models.DeviceDomainModel

class HandleDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(device: DeviceDomainModel): String {
        devicesRepository.register(device)
        return device.deviceId
    }
}
