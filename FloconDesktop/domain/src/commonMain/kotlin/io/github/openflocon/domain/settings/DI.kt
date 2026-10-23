package io.github.openflocon.domain.settings

import io.github.openflocon.domain.settings.usecase.GetLocalNetworkAddressesUseCase
import io.github.openflocon.domain.settings.usecase.InitAdbPathUseCase
import io.github.openflocon.domain.settings.usecase.ObserveAppScaleUseCase
import io.github.openflocon.domain.settings.usecase.ObserveMascotEnabledUseCase
import io.github.openflocon.domain.settings.usecase.ObserveThemeUseCase
import io.github.openflocon.domain.settings.usecase.SetAppScaleUseCase
import io.github.openflocon.domain.settings.usecase.SetMascotEnabledUseCase
import io.github.openflocon.domain.settings.usecase.SetThemeUseCase
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val settingsModule = module {
    factoryOf(::GetLocalNetworkAddressesUseCase)
    factoryOf(::InitAdbPathUseCase)
    factoryOf(::StartAdbForwardUseCase)
    factoryOf(::TestAdbUseCase)
    factoryOf(::ObserveAppScaleUseCase)
    factoryOf(::SetAppScaleUseCase)
    factoryOf(::ObserveThemeUseCase)
    factoryOf(::SetThemeUseCase)
    factoryOf(::ObserveMascotEnabledUseCase)
    factoryOf(::SetMascotEnabledUseCase)
}
