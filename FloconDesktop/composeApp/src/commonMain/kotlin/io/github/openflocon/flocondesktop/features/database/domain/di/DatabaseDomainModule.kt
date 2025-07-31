package io.github.openflocon.flocondesktop.features.database.domain.di

import io.github.openflocon.flocondesktop.features.database.domain.AskForDeviceDatabasesUseCase
import io.github.openflocon.flocondesktop.features.database.domain.ExecuteDatabaseQueryUseCase
import io.github.openflocon.flocondesktop.features.database.domain.GetCurrentDeviceSelectedDatabaseUseCase
import io.github.openflocon.flocondesktop.features.database.domain.ObserveCurrentDeviceSelectedDatabaseUseCase
import io.github.openflocon.flocondesktop.features.database.domain.ObserveDeviceDatabaseUseCase
import io.github.openflocon.flocondesktop.features.database.domain.ObserveLastSuccessQueriesUseCase
import io.github.openflocon.flocondesktop.features.database.domain.SelectCurrentDeviceDatabaseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val databaseDomainModule =
    module {
        factoryOf(::ExecuteDatabaseQueryUseCase)
        factoryOf(::ObserveDeviceDatabaseUseCase)
        factoryOf(::AskForDeviceDatabasesUseCase)
        factoryOf(::ObserveCurrentDeviceSelectedDatabaseUseCase)
        factoryOf(::GetCurrentDeviceSelectedDatabaseUseCase)
        factoryOf(::SelectCurrentDeviceDatabaseUseCase)
        factoryOf(::ObserveLastSuccessQueriesUseCase)
    }
