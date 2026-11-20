package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.models.settings.AppLanguageSetting
import io.github.openflocon.domain.settings.repository.SettingsRepository

class SetAppLanguageUseCase(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(value: AppLanguageSetting) {
        settingsRepository.setAppLanguage(value)
    }
}
