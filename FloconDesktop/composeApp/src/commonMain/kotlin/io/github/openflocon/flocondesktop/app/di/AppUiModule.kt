package io.github.openflocon.flocondesktop.app.di

import io.github.openflocon.flocondesktop.app.AppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appUiModule =
    module {
        viewModelOf(::AppViewModel)
    }
