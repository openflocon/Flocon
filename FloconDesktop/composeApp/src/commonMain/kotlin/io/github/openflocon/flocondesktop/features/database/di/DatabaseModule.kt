package io.github.openflocon.flocondesktop.features.database.di

import io.github.openflocon.flocondesktop.features.database.data.di.databaseDataModule
import io.github.openflocon.flocondesktop.features.database.ui.di.databaseUiModule
import org.koin.dsl.module

val databaseModule =
    module {
        includes(
            databaseDataModule,
            databaseUiModule,
        )
    }
