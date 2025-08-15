package io.github.openflocon.flocondesktop.features.images

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val imagesModule = module {
    viewModelOf(::ImagesViewModel)
}
