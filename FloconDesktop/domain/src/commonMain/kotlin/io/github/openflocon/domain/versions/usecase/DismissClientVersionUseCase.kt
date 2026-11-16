package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.FloconSdk

class DismissClientVersionUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(sdk: FloconSdk, version: String) {
        settingsRepository.setDismissedClientVersion(sdk, version)
    }
}
