package io.github.openflocon.domain.device.usecase.fps

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDeviceIsDisplayingFpsUseCase(
    private val devicesRepository: DevicesRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<Boolean> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest {
        if (it == null) {
            flowOf(false)
        } else {
            devicesRepository.observeIsDeviceDisplayingFps(it)
        }
    }
}
