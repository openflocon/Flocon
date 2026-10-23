package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceConnectionTypeDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.mergeConnectionTypes
import io.github.openflocon.domain.device.repository.DeviceConnectionTypesRepository
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveDeviceConnectionTypesUseCase(
    private val devicesRepository: DevicesRepository,
    private val deviceConnectionTypesRepository: DeviceConnectionTypesRepository,
) {
    operator fun invoke(): Flow<Map<DeviceId, DeviceConnectionTypeDomainModel>> = combine(
        devicesRepository.activeAddresses,
        deviceConnectionTypesRepository.lastConnectionTypes,
        ::mergeConnectionTypes,
    )
}
