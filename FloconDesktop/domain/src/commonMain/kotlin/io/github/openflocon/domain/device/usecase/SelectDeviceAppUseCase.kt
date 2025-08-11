package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository
import kotlinx.coroutines.flow.firstOrNull

class SelectDeviceAppUseCase(
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(packageName: String) {
        val app = devicesRepository.currentDevice
            .firstOrNull()
            ?.apps
            ?.find { it.packageName == packageName }
            ?: return

        devicesRepository.selectApp(app)
    }
}
