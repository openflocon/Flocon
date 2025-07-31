package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveCurrentDeviceIdUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceId?> = devicesRepository.currentDevice.map { it?.deviceId }.distinctUntilChanged()
}
