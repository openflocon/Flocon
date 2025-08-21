package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.models.DeviceWithAppDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel


class HandleDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(device: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel {
        return devicesRepository.register(device)
    }
}
