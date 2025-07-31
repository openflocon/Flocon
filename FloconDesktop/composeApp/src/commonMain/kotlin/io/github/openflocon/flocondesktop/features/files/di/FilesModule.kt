package io.github.openflocon.flocondesktop.features.files.di

import com.florent37.flocondesktop.features.files.data.di.filesDataModule
import com.florent37.flocondesktop.features.files.domain.di.filesDomainModule
import com.florent37.flocondesktop.features.files.ui.di.filesUiModule
import org.koin.dsl.module

val filesModule =
    module {
        includes(
            filesDataModule,
            filesDomainModule,
            filesUiModule,
        )
    }
