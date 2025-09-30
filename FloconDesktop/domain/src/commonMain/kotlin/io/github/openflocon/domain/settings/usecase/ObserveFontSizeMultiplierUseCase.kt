package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveFontSizeMultiplierUseCase(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): StateFlow<Float> = settingsRepository.fontSizeMultiplier

}
