package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.device.models.canTakeScreenshots
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Whether the selected device can take the screenshots of the timeline: not an Android connected over wifi, which has
 * no adb link. True while no device is selected or the device is not known yet.
 */
class ObserveTimelineScreenshotsSupportedUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val devicesRepository: DevicesRepository,
) {

    operator fun invoke(): Flow<Boolean> = combine(
        observeCurrentDeviceIdAndPackageNameUseCase(),
        devicesRepository.devices,
    ) { current, devices ->
        val device = current?.let { devices.firstOrNull { it.deviceId == current.deviceId } }
        device?.canTakeScreenshots() ?: true
    }.distinctUntilChanged()
}
