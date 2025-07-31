package io.github.openflocon.flocondesktop.features.deeplinks.domain.di

import io.github.openflocon.flocondesktop.features.deeplinks.domain.ExecuteDeeplinkUseCase
import io.github.openflocon.flocondesktop.features.deeplinks.domain.ObserveCurrentDeviceDeeplinkUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val deeplinkDomainModule =
    module {
        factoryOf(::ExecuteDeeplinkUseCase)
        factoryOf(::ObserveCurrentDeviceDeeplinkUseCase)
    }
