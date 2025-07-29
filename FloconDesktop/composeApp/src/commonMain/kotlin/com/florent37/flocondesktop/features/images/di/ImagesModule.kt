package com.florent37.flocondesktop.features.images.di

import com.florent37.flocondesktop.features.images.data.di.imagesDataModule
import com.florent37.flocondesktop.features.images.domain.di.imagesDomainModule
import com.florent37.flocondesktop.features.images.ui.di.imagesUiModule
import org.koin.dsl.module

val imagesModule =
    module {
        includes(
            imagesDataModule,
            imagesDomainModule,
            imagesUiModule,
        )
    }
