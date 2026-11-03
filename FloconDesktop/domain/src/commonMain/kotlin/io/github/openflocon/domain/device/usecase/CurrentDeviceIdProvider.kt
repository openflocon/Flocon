package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Where "the current device" comes from.
 * The main window follows the device selected in the top bar, a window opened for a device is pinned to it.
 */
interface CurrentDeviceIdProvider {
    fun observe(): Flow<DeviceId?>
}

class SelectedCurrentDeviceIdProvider(
    private val devicesRepository: DevicesRepository,
) : CurrentDeviceIdProvider {
    override fun observe(): Flow<DeviceId?> = devicesRepository.currentDeviceId
}
