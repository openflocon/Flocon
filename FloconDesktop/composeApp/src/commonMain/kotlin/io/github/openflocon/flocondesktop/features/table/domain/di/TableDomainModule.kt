package io.github.openflocon.flocondesktop.features.table.domain.di

import io.github.openflocon.domain.table.usecase.GetCurrentDeviceSelectedTableUseCase
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceSelectedTableUseCase
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceTableContentUseCase
import io.github.openflocon.domain.table.usecase.ObserveDeviceTablesUseCase
import io.github.openflocon.domain.table.usecase.ResetCurrentDeviceSelectedTableUseCase
import io.github.openflocon.domain.table.usecase.SelectCurrentDeviceTableUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val tableDomainModule = module {
    factoryOf(::GetCurrentDeviceSelectedTableUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedTableUseCase)
    factoryOf(::ObserveDeviceTablesUseCase)
    factoryOf(::SelectCurrentDeviceTableUseCase)
    factoryOf(::ObserveCurrentDeviceTableContentUseCase)
    factoryOf(::ResetCurrentDeviceSelectedTableUseCase)
}
