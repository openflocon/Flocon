package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository

class HandleDeviceAndAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(device: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel = devicesRepository.register(device)
}
