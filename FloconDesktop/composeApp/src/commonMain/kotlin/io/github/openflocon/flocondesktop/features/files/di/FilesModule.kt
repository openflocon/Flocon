package io.github.openflocon.flocondesktop.features.files.di

import io.github.openflocon.flocondesktop.features.files.data.di.filesDataModule
import io.github.openflocon.flocondesktop.features.files.domain.di.filesDomainModule
import io.github.openflocon.flocondesktop.features.files.ui.di.filesUiModule
import org.koin.dsl.module

val filesModule =
    module {
        includes(
            filesDataModule,
            filesDomainModule,
            filesUiModule,
        )
    }
