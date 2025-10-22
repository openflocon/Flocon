package io.github.openflocon.flocondesktop.menu.di

import io.github.openflocon.flocondesktop.menu.ui.di.mainUiModule
import org.koin.dsl.module

val mainModule = module {
    includes(
        mainUiModule
    )
}
