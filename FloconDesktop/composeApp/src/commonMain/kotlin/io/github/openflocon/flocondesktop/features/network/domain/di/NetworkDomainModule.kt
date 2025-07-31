package io.github.openflocon.flocondesktop.features.network.domain.di

import com.florent37.flocondesktop.features.network.domain.GenerateCurlCommandUseCase
import com.florent37.flocondesktop.features.network.domain.ObserveHttpRequestsByIdUseCase
import com.florent37.flocondesktop.features.network.domain.ObserveHttpRequestsUseCase
import com.florent37.flocondesktop.features.network.domain.RemoveHttpRequestUseCase
import com.florent37.flocondesktop.features.network.domain.RemoveHttpRequestsBeforeUseCase
import com.florent37.flocondesktop.features.network.domain.ResetCurrentDeviceHttpRequestsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val networkDomainModule =
    module {
        factoryOf(::ObserveHttpRequestsUseCase)
        factoryOf(::ObserveHttpRequestsByIdUseCase)
        factoryOf(::GenerateCurlCommandUseCase)
        factoryOf(::ResetCurrentDeviceHttpRequestsUseCase)
        factoryOf(::RemoveHttpRequestsBeforeUseCase)
        factoryOf(::RemoveHttpRequestUseCase)
    }
