package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveActiveDevicesUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<Set<DeviceIdAndPackageNameDomainModel>> = devicesRepository.activeDevices
}
