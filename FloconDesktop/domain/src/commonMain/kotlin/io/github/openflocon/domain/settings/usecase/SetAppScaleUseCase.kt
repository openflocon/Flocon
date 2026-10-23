package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.settings.repository.SettingsRepository

class SetAppScaleUseCase(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(value: AppScaleSetting) {
        settingsRepository.setAppScale(value)
    }
}
