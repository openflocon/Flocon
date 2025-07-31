package io.github.openflocon.flocondesktop.features.images.di

import io.github.openflocon.flocondesktop.features.images.data.di.imagesDataModule
import io.github.openflocon.flocondesktop.features.images.domain.di.imagesDomainModule
import io.github.openflocon.flocondesktop.features.images.ui.di.imagesUiModule
import org.koin.dsl.module

val imagesModule =
    module {
        includes(
            imagesDataModule,
            imagesDomainModule,
            imagesUiModule,
        )
    }
