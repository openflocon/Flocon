package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository

class SetAppLanguageUseCase(
    private val settingsRepository: SettingsRepository
) {

    /** @param languageTag BCP 47 tag, null to follow the system language. */
    suspend operator fun invoke(languageTag: String?) {
        settingsRepository.setAppLanguage(languageTag)
    }
}
