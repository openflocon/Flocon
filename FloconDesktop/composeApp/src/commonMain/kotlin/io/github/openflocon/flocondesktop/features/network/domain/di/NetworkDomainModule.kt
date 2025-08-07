package io.github.openflocon.flocondesktop.features.network.domain.di

import io.github.openflocon.flocondesktop.features.network.domain.GenerateCurlCommandUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ObserveHttpRequestsByIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ObserveHttpRequestsUseCase
import io.github.openflocon.flocondesktop.features.network.domain.RemoveHttpRequestUseCase
import io.github.openflocon.flocondesktop.features.network.domain.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.flocondesktop.features.network.domain.filter.GetNetworkFilterUseCase
import io.github.openflocon.flocondesktop.features.network.domain.filter.ObserveNetworkFilterUseCase
import io.github.openflocon.flocondesktop.features.network.domain.filter.UpdateNetworkFilterUseCase
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
