package com.florent37.flocondesktop.features.database.data.di

import com.florent37.flocondesktop.features.database.data.DatabaseRepositoryImpl
import com.florent37.flocondesktop.features.database.data.datasource.devicedatabases.DeviceDatabasesDataSource
import com.florent37.flocondesktop.features.database.data.datasource.devicedatabases.QueryDatabaseDataSource
import com.florent37.flocondesktop.features.database.data.datasource.local.LocalDatabaseDataSource
import com.florent37.flocondesktop.features.database.data.datasource.local.LocalDatabaseDataSourceRoom
import com.florent37.flocondesktop.features.database.domain.repository.DatabaseRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseDataModule =
    module {
        factoryOf(::DatabaseRepositoryImpl) {
            bind<DatabaseRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::DeviceDatabasesDataSource)
        singleOf(::QueryDatabaseDataSource)
        singleOf(::LocalDatabaseDataSourceRoom) {
            bind<LocalDatabaseDataSource>()
        }
    }
