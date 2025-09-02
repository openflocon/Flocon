package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveIsCurrentIsActiveUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): Flow<Boolean> = observeCurrentDeviceIdUseCase().flatMapLatest { currentDeviceId ->
        if(currentDeviceId == null) {
            flowOf(false)
        } else {
            devicesRepository.activeDevices
                .map { actives ->
                    actives.any { active -> active.deviceId == currentDeviceId }
                }
        }
    }.distinctUntilChanged()
}
