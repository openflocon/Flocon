package io.github.openflocon.flocondesktop.features.deeplinks.di

import io.github.openflocon.flocondesktop.features.deeplinks.data.di.deeplinkDataModule
import io.github.openflocon.flocondesktop.features.deeplinks.ui.di.deeplinkUiModule
import org.koin.dsl.module

val deeplinkModule =
    module {
        includes(
            deeplinkDataModule,
            deeplinkUiModule,
        )
    }
