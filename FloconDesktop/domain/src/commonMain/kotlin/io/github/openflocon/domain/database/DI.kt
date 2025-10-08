package io.github.openflocon.domain.database

import io.github.openflocon.domain.database.usecase.AskForDeviceDatabasesUseCase
import io.github.openflocon.domain.database.usecase.ExecuteDatabaseQueryUseCase
import io.github.openflocon.domain.database.usecase.GetCurrentDeviceSelectedDatabaseUseCase
import io.github.openflocon.domain.database.usecase.GetDeviceDatabaseTablesUseCase
import io.github.openflocon.domain.database.usecase.GetTableColumnsUseCase
import io.github.openflocon.domain.database.usecase.ObserveCurrentDeviceSelectedDatabaseAndTablesUseCase
import io.github.openflocon.domain.database.usecase.ObserveCurrentDeviceSelectedDatabaseUseCase
import io.github.openflocon.domain.database.usecase.ObserveDeviceDatabaseUseCase
import io.github.openflocon.domain.database.usecase.ObserveLastSuccessQueriesUseCase
import io.github.openflocon.domain.database.usecase.SelectCurrentDeviceDatabaseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val databaseModule = module {
    factoryOf(::ExecuteDatabaseQueryUseCase)
    factoryOf(::ObserveDeviceDatabaseUseCase)
    factoryOf(::AskForDeviceDatabasesUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedDatabaseUseCase)
    factoryOf(::GetCurrentDeviceSelectedDatabaseUseCase)
    factoryOf(::SelectCurrentDeviceDatabaseUseCase)
    factoryOf(::ObserveLastSuccessQueriesUseCase)
    factoryOf(::GetDeviceDatabaseTablesUseCase)
    factoryOf(::GetTableColumnsUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedDatabaseAndTablesUseCase)
}
