package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.CurrentDeviceAndAppDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveCurrentDeviceAndAppUseCase(
    private val devicesRepository: DevicesRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<CurrentDeviceAndAppDomainModel?> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(null)
        } else {
            combine(
                devicesRepository.devices.map { devices -> devices.firstOrNull { it.deviceId == deviceId } },
                devicesRepository.observeDeviceSelectedApp(deviceId),
            ) { device, app ->
                device?.let { CurrentDeviceAndAppDomainModel(device = it, app = app) }
            }
        }
    }
}
