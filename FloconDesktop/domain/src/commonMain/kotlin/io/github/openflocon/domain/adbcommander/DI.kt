package io.github.openflocon.domain.adbcommander

import io.github.openflocon.domain.adbcommander.usecase.ClearCommandHistoryUseCase
import io.github.openflocon.domain.adbcommander.usecase.DeleteFlowUseCase
import io.github.openflocon.domain.adbcommander.usecase.DeleteSavedCommandUseCase
import io.github.openflocon.domain.adbcommander.usecase.ExecuteAdbCommanderCommandUseCase
import io.github.openflocon.domain.adbcommander.usecase.ExecuteFlowUseCase
import io.github.openflocon.domain.adbcommander.usecase.ObserveCommandHistoryUseCase
import io.github.openflocon.domain.adbcommander.usecase.ObserveFlowsUseCase
import io.github.openflocon.domain.adbcommander.usecase.ObserveSavedCommandsUseCase
import io.github.openflocon.domain.adbcommander.usecase.SaveCommandUseCase
import io.github.openflocon.domain.adbcommander.usecase.SaveFlowUseCase
import io.github.openflocon.domain.adbcommander.usecase.UpdateFlowUseCase
import io.github.openflocon.domain.adbcommander.usecase.UpdateSavedCommandUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val adbCommanderModule = module {
    factoryOf(::ExecuteAdbCommanderCommandUseCase)
    factoryOf(::ObserveSavedCommandsUseCase)
    factoryOf(::SaveCommandUseCase)
    factoryOf(::DeleteSavedCommandUseCase)
    factoryOf(::UpdateSavedCommandUseCase)
    factoryOf(::ObserveCommandHistoryUseCase)
    factoryOf(::ClearCommandHistoryUseCase)
    factoryOf(::ObserveFlowsUseCase)
    factoryOf(::SaveFlowUseCase)
    factoryOf(::DeleteFlowUseCase)
    factoryOf(::UpdateFlowUseCase)
    factoryOf(::ExecuteFlowUseCase)
}
