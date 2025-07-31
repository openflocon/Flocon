package io.github.openflocon.flocondesktop.features.images.ui.di

import io.github.openflocon.flocondesktop.features.images.ui.ImagesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val imagesUiModule =
    module {
        viewModelOf(::ImagesViewModel)
    }
