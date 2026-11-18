package io.github.openflocon.domain.menu.usecase

import io.github.openflocon.domain.menu.repository.MenuPreferencesRepository
import kotlinx.coroutines.flow.first

class ToggleHiddenMenuItemUseCase(
    private val menuPreferencesRepository: MenuPreferencesRepository,
) {
    suspend operator fun invoke(itemId: String) {
        val hidden = menuPreferencesRepository.hiddenItems.first()
        menuPreferencesRepository.setHiddenItems(
            if (itemId in hidden) hidden - itemId else hidden + itemId,
        )
    }
}
