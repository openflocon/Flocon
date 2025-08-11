package io.github.openflocon.domain.deeplink

import io.github.openflocon.domain.deeplink.usecase.ExecuteDeeplinkUseCase
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val deeplinkModule = module {
    factoryOf(::ExecuteDeeplinkUseCase)
    factoryOf(::ObserveCurrentDeviceDeeplinkUseCase)
}
