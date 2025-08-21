package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel


class HandleDeviceAndAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(device: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel {
        return devicesRepository.register(device)
    }
}
