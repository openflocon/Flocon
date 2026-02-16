package io.github.openflocon.domain.commands

import io.github.openflocon.domain.commands.usecase.DeleteAdbCommandUseCase
import io.github.openflocon.domain.commands.usecase.InsertOrUpdateAdbCommandUseCase
import io.github.openflocon.domain.commands.usecase.ObserveAdbCommandsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val adbCommandModule = module {
    factoryOf(::ObserveAdbCommandsUseCase)
    factoryOf(::InsertOrUpdateAdbCommandUseCase)
    factoryOf(::DeleteAdbCommandUseCase)
}
