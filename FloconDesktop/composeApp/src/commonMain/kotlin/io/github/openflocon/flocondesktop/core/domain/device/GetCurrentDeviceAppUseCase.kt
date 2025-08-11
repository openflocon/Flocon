package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.models.DeviceAppDomainModel

class GetCurrentDeviceAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): DeviceAppDomainModel? = devicesRepository.getCurrentDeviceApp()
}
