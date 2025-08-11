package io.github.openflocon.flocondesktop.features.table.di

import io.github.openflocon.flocondesktop.features.table.data.di.tableDataModule
import io.github.openflocon.flocondesktop.features.table.ui.di.tableUiModule
import org.koin.dsl.module

val tableModule =
    module {
        includes(
            tableDataModule,
            tableUiModule,
        )
    }
