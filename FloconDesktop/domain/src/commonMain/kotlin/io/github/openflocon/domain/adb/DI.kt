package io.github.openflocon.domain.adb

import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val adbModule = module {
    factoryOf(::SendCommandUseCase)
}
