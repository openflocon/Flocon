package io.github.openflocon.domain.settings

import io.github.openflocon.domain.settings.usecase.InitAdbPathUseCase
import io.github.openflocon.domain.settings.usecase.ObserveFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.SetFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val settingsModule = module {
    factoryOf(::InitAdbPathUseCase)
    factoryOf(::StartAdbForwardUseCase)
    factoryOf(::TestAdbUseCase)
    factoryOf(::ObserveFontSizeMultiplierUseCase)
    factoryOf(::SetFontSizeMultiplierUseCase)
}
