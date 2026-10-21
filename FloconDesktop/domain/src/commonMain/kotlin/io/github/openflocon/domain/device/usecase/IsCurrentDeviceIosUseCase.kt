package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.isPlatformIos
import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.first

/**
 * Whether the device of [invoke] is an iOS one, whether it is the selected device or not
 */
class IsCurrentDeviceIosUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(current: DeviceIdAndPackageNameDomainModel): Boolean = devicesRepository.devices.first()
        .firstOrNull { it.deviceId == current.deviceId }
        ?.isPlatformIos() == true
}
