package io.github.openflocon.flocondesktop.features.deeplinks.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.core.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.features.deeplinks.domain.repository.DeeplinkRepository

class ExecuteDeeplinkUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val settingsRepository: SettingsRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase
) {
    operator fun invoke(deeplink: String) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val adbPath = settingsRepository.getAdbPath() ?: return

        deeplinkRepository.executeDeeplink(
            adbPath = adbPath,
            deviceIdAndPackageName = current,
            deeplink = deeplink,
        )
    }
}
