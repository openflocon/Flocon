package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Where "the current device" comes from, the only place the use cases read it.
 * The main window follows the device selected in the top bar, a window opened for a device is pinned to it.
 */
interface CurrentDeviceIdProvider {
    fun observe(): Flow<DeviceId?>
    suspend fun get(): DeviceId?
}

/** The current device, observed from the list of the devices. */
internal fun DevicesRepository.observeDevice(provider: CurrentDeviceIdProvider): Flow<DeviceDomainModel?> = provider.observe()
    .flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(null)
        } else {
            devices.map { devices -> devices.firstOrNull { it.deviceId == deviceId } }
        }
    }
    .distinctUntilChanged()

class SelectedCurrentDeviceIdProvider(
    private val devicesRepository: DevicesRepository,
) : CurrentDeviceIdProvider {
    override fun observe(): Flow<DeviceId?> = devicesRepository.currentDeviceId
    override suspend fun get(): DeviceId? = devicesRepository.getCurrentDeviceId()
}
