package io.github.openflocon.flocondesktop.features.table.domain.di

import com.florent37.flocondesktop.features.table.domain.GetCurrentDeviceSelectedTableUseCase
import com.florent37.flocondesktop.features.table.domain.ObserveCurrentDeviceSelectedTableUseCase
import com.florent37.flocondesktop.features.table.domain.ObserveCurrentDeviceTableContentUseCase
import com.florent37.flocondesktop.features.table.domain.ObserveDeviceTablesUseCase
import com.florent37.flocondesktop.features.table.domain.ResetCurrentDeviceSelectedTableUseCase
import com.florent37.flocondesktop.features.table.domain.SelectCurrentDeviceTableUseCase
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
