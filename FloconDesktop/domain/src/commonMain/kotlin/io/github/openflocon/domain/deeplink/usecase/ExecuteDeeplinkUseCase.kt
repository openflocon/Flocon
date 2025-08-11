package io.github.openflocon.domain.deeplink.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository

class ExecuteDeeplinkUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val settingsRepository: SettingsRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
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
