package com.florent37.flocondesktop.features.deeplinks.domain.di

import com.florent37.flocondesktop.features.deeplinks.domain.ExecuteDeeplinkUseCase
import com.florent37.flocondesktop.features.deeplinks.domain.ObserveCurrentDeviceDeeplinkUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val deeplinkDomainModule =
    module {
        factoryOf(::ExecuteDeeplinkUseCase)
        factoryOf(::ObserveCurrentDeviceDeeplinkUseCase)
    }
