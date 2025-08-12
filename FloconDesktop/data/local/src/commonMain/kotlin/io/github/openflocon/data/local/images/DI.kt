package io.github.openflocon.data.local.images

import io.github.openflocon.data.core.images.datasource.ImagesLocalDataSource
import io.github.openflocon.data.local.images.datasource.ImagesLocalDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val imagesModule = module {
    singleOf(::ImagesLocalDataSourceRoom) bind ImagesLocalDataSource::class
}
