package io.github.openflocon.domain.menu

import io.github.openflocon.domain.menu.usecase.ObserveMenuPreferencesUseCase
import io.github.openflocon.domain.menu.usecase.ToggleCollapsedMenuSectionUseCase
import io.github.openflocon.domain.menu.usecase.TogglePinnedMenuItemUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val menuModule = module {
    factoryOf(::ObserveMenuPreferencesUseCase)
    factoryOf(::TogglePinnedMenuItemUseCase)
    factoryOf(::ToggleCollapsedMenuSectionUseCase)
}
