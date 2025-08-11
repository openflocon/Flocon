package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import com.flocon.library.domain.models.DeviceDomainModel

class GetCurrentDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): DeviceDomainModel? = devicesRepository.getCurrentDevice()
}
