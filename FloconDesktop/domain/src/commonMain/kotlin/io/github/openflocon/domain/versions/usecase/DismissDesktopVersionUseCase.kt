package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository

class DismissDesktopVersionUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(version: String) {
        settingsRepository.setDismissedDesktopVersion(version)
    }
}
