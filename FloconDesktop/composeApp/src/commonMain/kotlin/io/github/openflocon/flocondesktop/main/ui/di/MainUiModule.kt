package io.github.openflocon.flocondesktop.main.ui.di

import io.github.openflocon.flocondesktop.main.ui.MainViewModel
import io.github.openflocon.flocondesktop.main.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.main.ui.nav.MainNavigator
import io.github.openflocon.flocondesktop.main.ui.nav.MainNavigatorImpl
import io.github.openflocon.flocondesktop.main.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainUiModule =
    module {
        viewModelOf(::MainViewModel)
        factoryOf(::DevicesDelegate)
        viewModelOf(::SettingsViewModel)
        singleOf(::MainNavigatorImpl) {
            bind<MainNavigator>()
        }
    }
