package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceFloconSdkVersionNameUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<String?> =
        observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
            if (current == null) {
                flowOf(null)
            } else {
                devicesRepository.observeDeviceSdkVersion(deviceId = current.deviceId, appPackageName = current.packageName)
            }
        }.distinctUntilChanged()
}
