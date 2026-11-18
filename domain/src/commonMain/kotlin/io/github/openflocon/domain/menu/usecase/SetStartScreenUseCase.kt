package io.github.openflocon.domain.menu.usecase

import io.github.openflocon.domain.menu.repository.MenuPreferencesRepository

class SetStartScreenUseCase(
    private val menuPreferencesRepository: MenuPreferencesRepository,
) {
    suspend operator fun invoke(screenId: String?) {
        menuPreferencesRepository.setStartScreen(screenId)
    }
}
