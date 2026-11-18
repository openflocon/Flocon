package io.github.openflocon.domain.images

import io.github.openflocon.domain.images.usecase.ObserveImagesUseCase
import io.github.openflocon.domain.images.usecase.ResetCurrentDeviceImagesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val imagesModule = module {
    factoryOf(::ObserveImagesUseCase)
    factoryOf(::ResetCurrentDeviceImagesUseCase)
}
