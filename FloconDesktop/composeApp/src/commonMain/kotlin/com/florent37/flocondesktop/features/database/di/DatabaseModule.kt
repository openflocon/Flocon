package com.florent37.flocondesktop.features.database.di

import com.florent37.flocondesktop.features.database.data.di.databaseDataModule
import com.florent37.flocondesktop.features.database.domain.di.databaseDomainModule
import com.florent37.flocondesktop.features.database.ui.di.databaseUiModule
import org.koin.dsl.module

val databaseModule =
    module {
        includes(
            databaseDataModule,
            databaseDomainModule,
            databaseUiModule,
        )
    }
