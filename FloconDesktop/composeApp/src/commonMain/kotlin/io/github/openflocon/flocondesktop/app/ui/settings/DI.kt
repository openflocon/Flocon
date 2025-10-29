package io.github.openflocon.flocondesktop.app.ui.settings

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
