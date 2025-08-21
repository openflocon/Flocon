package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveCurrentDeviceIdAndPackageNameUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<DeviceIdAndPackageNameDomainModel?> =
        observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
            if (deviceId == null) {
                flowOf(null)
            } else {
                devicesRepository.observeDeviceSelectedApp(deviceId)
                    .map { app ->
                        app?.let {
                            DeviceIdAndPackageNameDomainModel(
                                deviceId = deviceId,
                                packageName = app.packageName,
                            )
                        }
                    }
            }
        }.distinctUntilChanged()
}
