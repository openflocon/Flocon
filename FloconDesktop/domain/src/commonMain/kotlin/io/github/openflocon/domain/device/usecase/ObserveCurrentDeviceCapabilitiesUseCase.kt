package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceCapabilitiesDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.isPlatformAndroid
import io.github.openflocon.domain.device.models.isPlatformDesktop
import io.github.openflocon.domain.device.models.isPlatformIos
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveCurrentDeviceCapabilitiesUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceCapabilitiesDomainModel?> {
        return devicesRepository.observeCurrentDevice().flatMapLatest { device ->
            if (device == null) {
                flowOf(null)
            } else {
                devicesRepository.observeDeviceSelectedApp(device.deviceId).map { app ->
                    app?.let {
                        computeCapabilities(
                            device = device,
                            app = app,
                        )
                    }
                }

            }
        }
    }

    private fun computeCapabilities(
        device: DeviceDomainModel,
        app: DeviceAppDomainModel
    ): DeviceCapabilitiesDomainModel? {
        return if (device.isPlatformAndroid()) {
            DeviceCapabilitiesDomainModel(
                screenshot = true,
                recordScreen = true,
                restart = true,
                sharedPreferences = true,
                deeplinks = true,
                files = true,
            )
        } else if (device.isPlatformDesktop()) {
            DeviceCapabilitiesDomainModel(
                screenshot = false,
                recordScreen = false,
                restart = false,
                sharedPreferences = false,
                deeplinks = false,
                files = false,
            )
        } else if (device.isPlatformIos()) {
            DeviceCapabilitiesDomainModel(
                screenshot = false,
                recordScreen = false,
                restart = false,
                sharedPreferences = false, // may be possible on next versions
                deeplinks = false,
                files = false, // may be possible on next versions
            )
        } else {
            null
        }
    }
}
