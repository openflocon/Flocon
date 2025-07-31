package io.github.openflocon.flocondesktop.features.images.domain.di

import com.florent37.flocondesktop.features.images.domain.ObserveImagesUseCase
import com.florent37.flocondesktop.features.images.domain.ResetCurrentDeviceImagesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val imagesDomainModule =
    module {
        factoryOf(::ObserveImagesUseCase)
        factoryOf(::ResetCurrentDeviceImagesUseCase)
    }
