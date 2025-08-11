package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.models.DeviceAppDomainModel

class GetCurrentDeviceAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): DeviceAppDomainModel? = devicesRepository.getCurrentDeviceApp()
}
