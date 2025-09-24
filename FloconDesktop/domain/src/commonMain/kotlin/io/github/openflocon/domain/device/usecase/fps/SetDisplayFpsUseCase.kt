package io.github.openflocon.domain.device.usecase.fps

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class SetDisplayFpsUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val devicesRepository: DevicesRepository,
) {

    suspend operator fun invoke(value: Boolean) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        devicesRepository.sendDisplayFps(
            deviceIdAndPackageName = current,
            display = value,
        )

        devicesRepository.saveIsDeviceDisplayingFps(
            deviceIdAndPackageName = current,
            value = value,
        )
    }
}
