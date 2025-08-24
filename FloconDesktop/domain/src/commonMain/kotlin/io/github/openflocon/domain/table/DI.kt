package io.github.openflocon.domain.table

import io.github.openflocon.domain.table.usecase.GetCurrentDeviceSelectedTableUseCase
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceSelectedTableUseCase
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceTableContentUseCase
import io.github.openflocon.domain.table.usecase.ObserveDeviceTablesUseCase
import io.github.openflocon.domain.table.usecase.RemoveTableItemUseCase
import io.github.openflocon.domain.table.usecase.RemoveTableItemsBeforeUseCase
import io.github.openflocon.domain.table.usecase.ResetCurrentDeviceSelectedTableUseCase
import io.github.openflocon.domain.table.usecase.SelectCurrentDeviceTableUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val tableModule = module {
    factoryOf(::GetCurrentDeviceSelectedTableUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedTableUseCase)
    factoryOf(::ObserveDeviceTablesUseCase)
    factoryOf(::SelectCurrentDeviceTableUseCase)
    factoryOf(::ObserveCurrentDeviceTableContentUseCase)
    factoryOf(::ResetCurrentDeviceSelectedTableUseCase)
    factoryOf(::RemoveTableItemsBeforeUseCase)
    factoryOf(::RemoveTableItemUseCase)
}
