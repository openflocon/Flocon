package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveServerPortsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<ServerPortsDomainModel> = settingsRepository.serverPorts
        .distinctUntilChanged()
}
