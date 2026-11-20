package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.models.settings.AppLanguageSetting
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveAppLanguageUseCase(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): StateFlow<AppLanguageSetting> = settingsRepository.appLanguage
}
