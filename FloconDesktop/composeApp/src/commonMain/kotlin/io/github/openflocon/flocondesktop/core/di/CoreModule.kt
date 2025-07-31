package io.github.openflocon.flocondesktop.core.di

import io.github.openflocon.flocondesktop.core.data.di.coreDataModule
import io.github.openflocon.flocondesktop.core.domain.di.coreDomainModule
import org.koin.dsl.module

val coreModule =
    module {
        includes(
            coreDataModule,
            coreDomainModule,
        )
    }
