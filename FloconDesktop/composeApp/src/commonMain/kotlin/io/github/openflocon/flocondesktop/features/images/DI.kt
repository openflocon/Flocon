package io.github.openflocon.flocondesktop.features.images

import io.github.openflocon.flocondesktop.features.images.processor.DownloadImageProcessor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val imagesModule = module {
    factoryOf(::DownloadImageProcessor)
    viewModelOf(::ImagesViewModel)
}
