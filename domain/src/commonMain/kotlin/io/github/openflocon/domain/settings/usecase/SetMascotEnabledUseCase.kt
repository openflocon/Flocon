package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.MascotPreferencesRepository

class SetMascotEnabledUseCase(
    private val mascotPreferencesRepository: MascotPreferencesRepository,
) {

    suspend operator fun invoke(enabled: Boolean) {
        mascotPreferencesRepository.setEnabled(enabled)
    }
}
