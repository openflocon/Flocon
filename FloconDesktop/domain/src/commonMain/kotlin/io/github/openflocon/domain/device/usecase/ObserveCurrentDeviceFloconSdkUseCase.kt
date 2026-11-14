package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.versions.model.FloconSdk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/** The SDK (Kotlin, Swift, Flutter) of the selected app, null when there is none or when it is not known yet. */
class ObserveCurrentDeviceFloconSdkUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<FloconSdk?> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
        if (current == null) {
            flowOf(null)
        } else {
            devicesRepository.observeDeviceSdk(deviceId = current.deviceId, appPackageName = current.packageName)
        }
    }.distinctUntilChanged()
}
