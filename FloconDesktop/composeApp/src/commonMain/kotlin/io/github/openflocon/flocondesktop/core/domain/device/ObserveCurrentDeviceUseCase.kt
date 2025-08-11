package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import com.flocon.library.domain.models.DeviceDomainModel
import kotlinx.coroutines.flow.Flow

class ObserveCurrentDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceDomainModel?> = devicesRepository.currentDevice
}
