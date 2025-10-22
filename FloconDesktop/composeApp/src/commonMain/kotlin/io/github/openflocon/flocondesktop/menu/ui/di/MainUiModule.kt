package io.github.openflocon.flocondesktop.menu.ui.di

import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailViewModel
import io.github.openflocon.flocondesktop.menu.MainRoutes
import io.github.openflocon.flocondesktop.menu.ui.MainViewModel
import io.github.openflocon.flocondesktop.menu.ui.MenuNavigationState
import io.github.openflocon.flocondesktop.menu.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.menu.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen
import io.github.openflocon.flocondesktop.menu.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainUiModule = module {
    scope<MainRoutes.Sub> {
        scoped { MenuNavigationState(SubScreen.Network) }
        viewModelOf(::MainViewModel)
        factoryOf(::DevicesDelegate)
        factoryOf(::RecordVideoDelegate)
        viewModelOf(::SettingsViewModel)

        viewModelOf(::NetworkDetailViewModel)
    }
}
