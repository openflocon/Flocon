package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import com.flocon.library.domain.models.DeviceAppDomainModel

class GetCurrentDeviceAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): DeviceAppDomainModel? = devicesRepository.getCurrentDeviceApp()
}
