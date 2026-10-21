package io.github.openflocon.domain.menu.usecase

import io.github.openflocon.domain.menu.repository.MenuPreferencesRepository
import kotlinx.coroutines.flow.first

class ToggleCollapsedMenuSectionUseCase(
    private val menuPreferencesRepository: MenuPreferencesRepository,
) {
    suspend operator fun invoke(sectionId: String) {
        val collapsed = menuPreferencesRepository.collapsedSections.first()
        menuPreferencesRepository.setCollapsedSections(
            if (sectionId in collapsed) collapsed - sectionId else collapsed + sectionId,
        )
    }
}
