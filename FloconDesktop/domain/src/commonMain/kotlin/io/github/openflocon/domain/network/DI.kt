package io.github.openflocon.domain.network

import io.github.openflocon.domain.network.usecase.DecodeJwtTokenUseCase
import io.github.openflocon.domain.network.usecase.ExportNetworkCallsToCsvUseCase
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.GetNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.GetNetworkRequestsUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsByIdUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.RemoveNetworkRequestUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.domain.network.usecase.RemoveOldSessionsNetworkRequestUseCase
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
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkWebsocketIdsUseCase
import io.github.openflocon.domain.network.usecase.mocks.SendNetworkWebsocketMockUseCase
import io.github.openflocon.domain.network.usecase.mocks.SetupNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.UpdateNetworkMockIsEnabledUseCase
import io.github.openflocon.domain.network.usecase.mocks.UpdateNetworkMocksDeviceUseCase
import io.github.openflocon.domain.network.usecase.settings.GetNetworkSettingsUseCase
import io.github.openflocon.domain.network.usecase.settings.ObserveNetworkSettingsUseCase
import io.github.openflocon.domain.network.usecase.settings.UpdateNetworkSettingsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val networkModule = module {
    factoryOf(::ObserveNetworkRequestsUseCase)
    factoryOf(::GetNetworkRequestsUseCase)
    factoryOf(::ObserveNetworkRequestsByIdUseCase)
    factoryOf(::GenerateCurlCommandUseCase)
    factoryOf(::ResetCurrentDeviceHttpRequestsUseCase)
    factoryOf(::RemoveHttpRequestsBeforeUseCase)
    factoryOf(::RemoveNetworkRequestUseCase)
    factoryOf(::ExportNetworkCallsToCsvUseCase)
    factoryOf(::DecodeJwtTokenUseCase)
    factoryOf(::RemoveOldSessionsNetworkRequestUseCase)
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
    factoryOf(::UpdateNetworkMocksDeviceUseCase)
    factoryOf(::ObserveNetworkWebsocketIdsUseCase)
    factoryOf(::SendNetworkWebsocketMockUseCase)
    // bad quality
    factoryOf(::ObserveNetworkBadQualityUseCase)
    factoryOf(::SaveNetworkBadQualityUseCase)
    factoryOf(::DeleteBadQualityUseCase)
    factoryOf(::SetupNetworkBadQualityUseCase)
    factoryOf(::SetNetworkBadQualityEnabledConfigUseCase)
    factoryOf(::ObserveAllNetworkBadQualitiesUseCase)
    // settings
    factoryOf(::GetNetworkSettingsUseCase)
    factoryOf(::UpdateNetworkSettingsUseCase)
    factoryOf(::ObserveNetworkSettingsUseCase)
}
