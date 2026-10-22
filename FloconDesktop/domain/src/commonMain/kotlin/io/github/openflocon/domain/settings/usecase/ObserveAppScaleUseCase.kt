package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveAppScaleUseCase(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): StateFlow<AppScaleSetting> = settingsRepository.appScale
}
