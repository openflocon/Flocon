package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** The effective ports: the launch options win over the saved ports, for this session only. */
class ObserveServerPortsUseCase(
    private val settingsRepository: SettingsRepository,
    private val overrides: ServerPortsOverrides,
) {
    operator fun invoke(): Flow<ServerPortsDomainModel> = settingsRepository.savedServerPorts
        .map { overrides.applyTo(it) }
        .distinctUntilChanged()
}
