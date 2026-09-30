package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository

class DismissClientVersionUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(version: String) {
        settingsRepository.setDismissedClientVersion(version)
    }
}
