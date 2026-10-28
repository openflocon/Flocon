package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceCapabilitiesDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.isPlatformAndroid
import io.github.openflocon.domain.device.models.isPlatformDesktop
import io.github.openflocon.domain.device.models.isPlatformIos
import io.github.openflocon.domain.device.models.supportsAdb
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveCurrentDeviceCapabilitiesUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceCapabilitiesDomainModel?> = devicesRepository.observeCurrentDevice().flatMapLatest { device ->
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

internal fun computeCapabilities(
    device: DeviceDomainModel,
    app: DeviceAppDomainModel
): DeviceCapabilitiesDomainModel? = when {
    device.isPlatformAndroid() -> {
        val adb = device.supportsAdb()
        DeviceCapabilitiesDomainModel(
            screenshot = adb,
            recordScreen = adb,
            restart = true,
            sharedPreferences = true,
            deeplinks = adb,
            adbAvailable = adb,
            files = true,
        )
    }

    device.isPlatformDesktop() -> DeviceCapabilitiesDomainModel(
        screenshot = false,
        recordScreen = false,
        restart = false,
        sharedPreferences = false,
        deeplinks = false,
        files = false,
    )

    device.isPlatformIos() -> DeviceCapabilitiesDomainModel(
        screenshot = true, // iOS Simulator only, through `xcrun simctl io screenshot`
        recordScreen = true, // iOS Simulator only, through `xcrun simctl io recordVideo`
        restart = false,
        sharedPreferences = false, // may be possible on next versions
        deeplinks = true, // iOS Simulator only, through `xcrun simctl openurl`
        files = false, // may be possible on next versions
    )

    else -> null
}
