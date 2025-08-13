package io.github.openflocon.flocondesktop.features.network.di

import io.github.openflocon.flocondesktop.features.network.ui.di.networkUiModule
import org.koin.dsl.module

val networkModule = module {
    includes(
        networkUiModule,
    )
}
