package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceAppsUseCase(
    private val devicesRepository: DevicesRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<DeviceAppDomainModel>> = observeCurrentDeviceIdUseCase().flatMapLatest {
        if (it == null) {
            flowOf(emptyList())
        } else {
            devicesRepository.observeDeviceApps(it)
        }
    }
}
