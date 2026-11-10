package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.MascotPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveMascotEnabledUseCase(
    private val mascotPreferencesRepository: MascotPreferencesRepository,
) {

    operator fun invoke(): Flow<Boolean> = mascotPreferencesRepository.isEnabled
}
