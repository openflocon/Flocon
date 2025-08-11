package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.models.DeviceDomainModel

class GetCurrentDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    operator fun invoke(): DeviceDomainModel? = devicesRepository.getCurrentDevice()
}
