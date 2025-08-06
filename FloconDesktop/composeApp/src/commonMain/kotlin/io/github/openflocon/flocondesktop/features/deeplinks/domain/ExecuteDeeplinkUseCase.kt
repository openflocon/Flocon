package io.github.openflocon.flocondesktop.features.deeplinks.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceUseCase
import io.github.openflocon.flocondesktop.core.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.features.deeplinks.domain.repository.DeeplinkRepository

class ExecuteDeeplinkUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val settingsRepository: SettingsRepository,
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
    private val getCurrentDeviceAppUseCase: GetCurrentDeviceAppUseCase
) {
    operator fun invoke(deeplink: String) {
        val device = getCurrentDeviceUseCase() ?: return
        val app = getCurrentDeviceAppUseCase() ?: return
        val adbPath = settingsRepository.getAdbPath() ?: return

        deeplinkRepository.executeDeeplink(
            adbPath = adbPath,
            deviceId = device.deviceId,
            packageName = app.packageName,
            deeplink = deeplink,
        )
    }
}
