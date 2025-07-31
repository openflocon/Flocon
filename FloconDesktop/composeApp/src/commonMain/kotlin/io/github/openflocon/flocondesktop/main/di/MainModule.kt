package io.github.openflocon.flocondesktop.main.di

import io.github.openflocon.flocondesktop.main.ui.di.mainUiModule
import org.koin.dsl.module

val mainModule =
    module {
        includes(
            mainUiModule,
        )
    }
