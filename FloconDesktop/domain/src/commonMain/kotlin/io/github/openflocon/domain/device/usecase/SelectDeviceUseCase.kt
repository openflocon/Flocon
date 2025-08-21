package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.firstOrNull

class SelectDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(deviceId: String) {
        devicesRepository.selectDevice(deviceId)
    }
}
