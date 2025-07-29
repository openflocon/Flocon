package com.florent37.flocondesktop.features.table.di

import com.florent37.flocondesktop.features.table.data.di.tableDataModule
import com.florent37.flocondesktop.features.table.domain.di.tableDomainModule
import com.florent37.flocondesktop.features.table.ui.di.tableUiModule
import org.koin.dsl.module

val tableModule =
    module {
        includes(
            tableDataModule,
            tableDomainModule,
            tableUiModule,
        )
    }
