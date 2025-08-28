package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DevicesRepository

class DeleteDeviceApplicationUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(
        deviceId: DeviceId,
        packageName: AppPackageName,
    ) {
        devicesRepository.deleteApplication(
            deviceId = deviceId,
            packageName = packageName,
        )
    }
}
