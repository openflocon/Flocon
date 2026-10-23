package io.github.openflocon.domain.menu.usecase

import io.github.openflocon.domain.menu.models.MenuPreferencesDomainModel
import io.github.openflocon.domain.menu.repository.MenuPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveMenuPreferencesUseCase(
    private val menuPreferencesRepository: MenuPreferencesRepository,
) {
    operator fun invoke(): Flow<MenuPreferencesDomainModel> = combine(
        menuPreferencesRepository.pinnedItems,
        menuPreferencesRepository.collapsedSections,
    ) { pinned, collapsed ->
        MenuPreferencesDomainModel(
            pinnedItems = pinned,
            collapsedSections = collapsed,
        )
    }
}
