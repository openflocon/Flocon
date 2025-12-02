package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.repository.DevicesRepository

class SelectDeviceAppUseCase(
    private val devicesRepository: DevicesRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(packageName: String) {
        val deviceId = getCurrentDeviceIdUseCase() ?: return

        val app = devicesRepository.getDeviceAppByPackage(deviceId = deviceId, appPackageName = packageName) ?: return

        devicesRepository.selectApp(deviceId = deviceId, app = app)
    }
}
