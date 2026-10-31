package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * The plugins installed in the SDK of the selected app, null when unknown
 * (no app selected, or an SDK that does not send its plugins).
 */
class ObserveCurrentDeviceInstalledPluginsUseCase(
    private val devicesRepository: DevicesRepository,
    private val currentDeviceIdProvider: CurrentDeviceIdProvider,
) {
    operator fun invoke(): Flow<Set<String>?> = devicesRepository.observeDevice(currentDeviceIdProvider)
        .flatMapLatest { device ->
            if (device == null) {
                flowOf(null)
            } else {
                devicesRepository.observeDeviceSelectedApp(device.deviceId).map { it?.installedPlugins }
            }
        }
        .distinctUntilChanged()
}
