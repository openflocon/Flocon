package io.github.openflocon.flocondesktop.core.data.settings.usecase

import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveNetworkSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<NetworkSettings> = settingsRepository.networkSettingsFlow

}
