package io.github.openflocon.domain.network

import io.github.openflocon.domain.network.usecase.ExportNetworkCallsToCsvUseCase
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.GetNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsByIdUseCase
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.domain.network.usecase.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.UpdateNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.badquality.DeleteBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveAllNetworkBadQualitiesUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.SaveNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.SetupNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.SetNetworkBadQualityEnabledConfigUseCase
import io.github.openflocon.domain.network.usecase.mocks.AddNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.DeleteNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.GenerateNetworkMockFromNetworkCallUseCase
import io.github.openflocon.domain.network.usecase.mocks.GetNetworkMockByIdUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.SetupNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.UpdateNetworkMockIsEnabledUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val networkModule = module {
    factoryOf(::ObserveHttpRequestsUseCase)
    factoryOf(::ObserveHttpRequestsByIdUseCase)
    factoryOf(::GenerateCurlCommandUseCase)
    factoryOf(::ResetCurrentDeviceHttpRequestsUseCase)
    factoryOf(::RemoveHttpRequestsBeforeUseCase)
    factoryOf(::RemoveHttpRequestUseCase)
    factoryOf(::ExportNetworkCallsToCsvUseCase)
    // filters
    factoryOf(::GetNetworkFilterUseCase)
    factoryOf(::ObserveNetworkFilterUseCase)
    factoryOf(::UpdateNetworkFilterUseCase)
    // mocks
    factoryOf(::ObserveNetworkMocksUseCase)
    factoryOf(::AddNetworkMocksUseCase)
    factoryOf(::DeleteNetworkMocksUseCase)
    factoryOf(::SetupNetworkMocksUseCase)
    factoryOf(::GetNetworkMockByIdUseCase)
    factoryOf(::GenerateNetworkMockFromNetworkCallUseCase)
    factoryOf(::UpdateNetworkMockIsEnabledUseCase)
    // bad quality
    factoryOf(::ObserveNetworkBadQualityUseCase)
    factoryOf(::SaveNetworkBadQualityUseCase)
    factoryOf(::DeleteBadQualityUseCase)
    factoryOf(::SetupNetworkBadQualityUseCase)
    factoryOf(::SetNetworkBadQualityEnabledConfigUseCase)
    factoryOf(::ObserveAllNetworkBadQualitiesUseCase)
}
