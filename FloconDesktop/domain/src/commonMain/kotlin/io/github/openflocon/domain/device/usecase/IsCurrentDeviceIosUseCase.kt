package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.isPlatformIos
import io.github.openflocon.domain.device.repository.DevicesRepository

class IsCurrentDeviceIosUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(current: DeviceIdAndPackageNameDomainModel): Boolean = devicesRepository.getCurrentDevice()
        ?.takeIf { it.deviceId == current.deviceId }
        ?.isPlatformIos() == true
}
