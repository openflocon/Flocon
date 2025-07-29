package com.florent37.flocondesktop.core.domain.device

import com.florent37.flocondesktop.core.domain.device.repository.DevicesRepository
import com.florent37.flocondesktop.messages.domain.model.DeviceDomainModel
import kotlinx.coroutines.flow.Flow

class ObserveCurrentDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceDomainModel?> = devicesRepository.currentDevice
}
