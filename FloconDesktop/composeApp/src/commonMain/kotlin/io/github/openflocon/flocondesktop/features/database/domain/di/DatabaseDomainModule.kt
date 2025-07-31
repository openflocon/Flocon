package io.github.openflocon.flocondesktop.features.database.domain.di

import com.florent37.flocondesktop.features.database.domain.AskForDeviceDatabasesUseCase
import com.florent37.flocondesktop.features.database.domain.ExecuteDatabaseQueryUseCase
import com.florent37.flocondesktop.features.database.domain.GetCurrentDeviceSelectedDatabaseUseCase
import com.florent37.flocondesktop.features.database.domain.ObserveCurrentDeviceSelectedDatabaseUseCase
import com.florent37.flocondesktop.features.database.domain.ObserveDeviceDatabaseUseCase
import com.florent37.flocondesktop.features.database.domain.ObserveLastSuccessQueriesUseCase
import com.florent37.flocondesktop.features.database.domain.SelectCurrentDeviceDatabaseUseCase
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
