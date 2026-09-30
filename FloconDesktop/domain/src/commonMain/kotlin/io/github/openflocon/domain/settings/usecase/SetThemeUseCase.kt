package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.SettingsRepository

class SetThemeUseCase(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(value: ThemeSetting) {
        settingsRepository.setTheme(value)
    }
}
