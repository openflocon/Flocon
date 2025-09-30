package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository

class SetFontSizeMultiplierUseCase(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(value: Float) {
        settingsRepository.setFontSizeMultiplier(value)
    }
}
