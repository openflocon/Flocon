package io.github.openflocon.flocondesktop.core.di

import io.github.openflocon.flocondesktop.core.data.di.coreDataModule
import org.koin.dsl.module

val coreModule = module {
    includes(
        coreDataModule
    )
}
