package io.github.openflocon.flocondesktop.features.network.domain.di

import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsByIdUseCase
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.domain.network.usecase.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.GetNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.UpdateNetworkFilterUseCase
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
        factoryOf(::GetNetworkFilterUseCase)
        factoryOf(::ObserveNetworkFilterUseCase)
        factoryOf(::UpdateNetworkFilterUseCase)
    }
