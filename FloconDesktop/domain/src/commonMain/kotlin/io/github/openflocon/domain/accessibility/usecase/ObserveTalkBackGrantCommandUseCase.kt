package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.device.models.isPlatformAndroid
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * The adb command to run once to let the app of the current device toggle TalkBack, when Flocon cannot run it by itself:
 * an Android device connected over wifi that cannot toggle TalkBack yet. null otherwise.
 */
class ObserveTalkBackGrantCommandUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val observeCurrentDeviceAccessibilityStateUseCase: ObserveCurrentDeviceAccessibilityStateUseCase,
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<String?> = combine(
        observeCurrentDeviceIdAndPackageNameUseCase(),
        devicesRepository.observeCurrentDevice(),
        observeCurrentDeviceAccessibilityStateUseCase(),
    ) { current, device, state ->
        if (current != null &&
            device != null &&
            device.isPlatformAndroid() &&
            device.connectionType == DeviceConnectionType.Wifi &&
            state != null &&
            !state.canToggleTalkBack
        ) {
            "adb shell pm grant ${current.packageName} $WRITE_SECURE_SETTINGS"
        } else {
            null
        }
    }.distinctUntilChanged()
}
