package io.github.openflocon.domain.menu.usecase

import io.github.openflocon.domain.menu.repository.MenuPreferencesRepository
import kotlinx.coroutines.flow.first

class TogglePinnedMenuItemUseCase(
    private val menuPreferencesRepository: MenuPreferencesRepository,
) {
    suspend operator fun invoke(itemId: String) {
        val pinned = menuPreferencesRepository.pinnedItems.first()
        menuPreferencesRepository.setPinnedItems(
            if (itemId in pinned) pinned - itemId else pinned + itemId,
        )
    }
}
