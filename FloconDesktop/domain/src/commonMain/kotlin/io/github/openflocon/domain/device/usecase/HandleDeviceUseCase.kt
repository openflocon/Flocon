package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel


class HandleDeviceUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(device: DeviceDomainModel): HandleDeviceResultDomainModel {
        val isNewDevice = devicesRepository.register(device)
        return HandleDeviceResultDomainModel(
            deviceId = device.deviceId,
            isNewDevice = isNewDevice,
        )
    }
}
