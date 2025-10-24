package io.github.openflocon.flocondesktop.core.data.settings.usecase

import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.settings.repository.SettingsRepository

class SaveNetworkSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(
        settings: NetworkSettings
    ): Result<Unit> = runCatching {
        settingsRepository.networkSettings = settings
    }

}
