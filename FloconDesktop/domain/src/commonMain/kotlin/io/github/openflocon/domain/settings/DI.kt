package io.github.openflocon.domain.settings

import io.github.openflocon.domain.settings.usecase.InitAdbPathUseCase
import io.github.openflocon.domain.settings.usecase.ObserveAppScaleUseCase
import io.github.openflocon.domain.settings.usecase.ObserveMascotEnabledUseCase
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import io.github.openflocon.domain.settings.usecase.ObserveThemeUseCase
import io.github.openflocon.domain.settings.usecase.ObserveTransactionsExpandedByDefaultUseCase
import io.github.openflocon.domain.settings.usecase.SetAppScaleUseCase
import io.github.openflocon.domain.settings.usecase.SetMascotEnabledUseCase
import io.github.openflocon.domain.settings.usecase.SetThemeUseCase
import io.github.openflocon.domain.settings.usecase.SetTransactionsExpandedByDefaultUseCase
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import io.github.openflocon.domain.settings.usecase.UpdateServerPortsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val settingsModule = module {
    factoryOf(::InitAdbPathUseCase)
    factoryOf(::StartAdbForwardUseCase)
    factoryOf(::TestAdbUseCase)
    factoryOf(::ObserveServerPortsUseCase)
    factoryOf(::UpdateServerPortsUseCase)
    factoryOf(::ObserveAppScaleUseCase)
    factoryOf(::SetAppScaleUseCase)
    factoryOf(::ObserveThemeUseCase)
    factoryOf(::SetThemeUseCase)
    factoryOf(::ObserveMascotEnabledUseCase)
    factoryOf(::SetMascotEnabledUseCase)
    factoryOf(::ObserveTransactionsExpandedByDefaultUseCase)
    factoryOf(::SetTransactionsExpandedByDefaultUseCase)
}
