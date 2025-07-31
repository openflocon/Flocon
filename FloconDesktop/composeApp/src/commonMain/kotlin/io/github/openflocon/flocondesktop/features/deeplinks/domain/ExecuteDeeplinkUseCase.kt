package io.github.openflocon.flocondesktop.features.deeplinks.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceUseCase
import com.florent37.flocondesktop.core.domain.settings.repository.SettingsRepository
import com.florent37.flocondesktop.features.deeplinks.domain.repository.DeeplinkRepository

class ExecuteDeeplinkUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val settingsRepository: SettingsRepository,
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
) {
    operator fun invoke(deeplink: String) {
        val device = getCurrentDeviceUseCase() ?: return
        val adbPath = settingsRepository.getAdbPath() ?: return
        deeplinkRepository.executeDeeplink(
            adbPath = adbPath,
            deviceId = device.deviceId,
            packageName = device.appPackageName,
            deeplink = deeplink,
        )
    }
}
