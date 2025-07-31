package io.github.openflocon.flocondesktop.main.ui.di

import com.florent37.flocondesktop.main.ui.MainViewModel
import com.florent37.flocondesktop.main.ui.delegates.DevicesDelegate
import com.florent37.flocondesktop.main.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainUiModule =
    module {
        viewModelOf(::MainViewModel)
        factoryOf(::DevicesDelegate)
        viewModelOf(::SettingsViewModel)
    }
