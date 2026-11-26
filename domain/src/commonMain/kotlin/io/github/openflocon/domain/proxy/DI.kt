package io.github.openflocon.domain.proxy

import io.github.openflocon.domain.proxy.usecase.ObserveProxyEnabledUseCase
import io.github.openflocon.domain.proxy.usecase.ObserveProxyStateUseCase
import io.github.openflocon.domain.proxy.usecase.RunProxyUseCase
import io.github.openflocon.domain.proxy.usecase.SetProxyEnabledUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val proxyModule = module {
    factoryOf(::ObserveProxyStateUseCase)
    factoryOf(::ObserveProxyEnabledUseCase)
    factoryOf(::SetProxyEnabledUseCase)
    factoryOf(::RunProxyUseCase)
}
