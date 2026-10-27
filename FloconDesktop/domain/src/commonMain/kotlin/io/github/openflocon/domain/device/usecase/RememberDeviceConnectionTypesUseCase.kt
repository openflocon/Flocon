package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.mergeConnectionTypes
import io.github.openflocon.domain.device.repository.DeviceConnectionTypesRepository
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.first

/**
 * Keeps the connection type of the devices as they connect, so an offline device stays in the tab it last used.
 * Never returns: collects the active devices.
 */
class RememberDeviceConnectionTypesUseCase(
    private val devicesRepository: DevicesRepository,
    private val deviceConnectionTypesRepository: DeviceConnectionTypesRepository,
) {
    suspend operator fun invoke() {
        devicesRepository.activeAddresses.collect { activeAddresses ->
            val known = deviceConnectionTypesRepository.lastConnectionTypes.first()
            val updated = mergeConnectionTypes(activeAddresses = activeAddresses, lastTypes = known)
            if (updated != known) {
                deviceConnectionTypesRepository.setConnectionTypes(updated)
            }
        }
    }
}
