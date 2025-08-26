package io.github.openflocon.domain.adb

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val adbModule = module {
    factoryOf(::ExecuteAdbCommandUseCase)
    factoryOf(::StartAdbProcessUseCase)
    factoryOf(::StopAdbProcessUseCase)
}
