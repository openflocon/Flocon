package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.firstOrNull

class SelectDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(deviceId: String) {
        val device = devicesRepository.devices
            .firstOrNull()
            ?.find { it.deviceId == deviceId }
            ?: return

        devicesRepository.selectDevice(device)
    }
}
