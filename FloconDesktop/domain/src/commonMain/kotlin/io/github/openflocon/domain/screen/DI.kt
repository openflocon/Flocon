package io.github.openflocon.domain.screen

import io.github.openflocon.domain.screen.usecase.ObserveRequestedDesktopScreenUseCase
import io.github.openflocon.domain.screen.usecase.RequestDesktopScreenUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val screenModule = module {
    factoryOf(::RequestDesktopScreenUseCase)
    factoryOf(::ObserveRequestedDesktopScreenUseCase)
}
