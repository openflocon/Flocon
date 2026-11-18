package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveAppLanguageUseCase(
    private val settingsRepository: SettingsRepository
) {

    /** BCP 47 tag of the chosen language, null to follow the system one. */
    operator fun invoke(): StateFlow<String?> = settingsRepository.appLanguage
}
