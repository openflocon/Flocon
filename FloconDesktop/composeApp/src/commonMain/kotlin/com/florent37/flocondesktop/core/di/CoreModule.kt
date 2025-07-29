package com.florent37.flocondesktop.core.di

import com.florent37.flocondesktop.core.data.di.coreDataModule
import com.florent37.flocondesktop.core.domain.di.coreDomainModule
import org.koin.dsl.module

val coreModule =
    module {
        includes(
            coreDataModule,
            coreDomainModule,
        )
    }
