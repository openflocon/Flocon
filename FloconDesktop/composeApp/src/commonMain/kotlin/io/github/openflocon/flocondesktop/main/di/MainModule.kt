package io.github.openflocon.flocondesktop.main.di

import com.florent37.flocondesktop.main.ui.di.mainUiModule
import org.koin.dsl.module

val mainModule =
    module {
        includes(
            mainUiModule,
        )
    }
