package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveThemeUseCase(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): StateFlow<ThemeSetting> = settingsRepository.theme
}
