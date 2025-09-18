package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository

class RestartAppUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val devicesRepository: DevicesRepository,
) {

    suspend operator fun invoke() {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        devicesRepository.restartApp(
            current
        )
    }
}
