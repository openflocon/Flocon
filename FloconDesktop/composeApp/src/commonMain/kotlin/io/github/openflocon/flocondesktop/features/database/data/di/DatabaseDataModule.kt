package io.github.openflocon.flocondesktop.features.database.data.di

import io.github.openflocon.flocondesktop.features.database.data.DatabaseRepositoryImpl
import io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases.DeviceDatabasesRemoteDataSourceImpl
import io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases.QueryDatabaseRemoteDataSourceImpl
import io.github.openflocon.flocondesktop.features.database.data.datasource.local.LocalDatabaseDataSource
import io.github.openflocon.flocondesktop.features.database.data.datasource.local.LocalDatabaseDataSourceRoom
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
        singleOf(::DeviceDatabasesRemoteDataSourceImpl)
        singleOf(::QueryDatabaseRemoteDataSourceImpl)
        singleOf(::LocalDatabaseDataSourceRoom) {
            bind<LocalDatabaseDataSource>()
        }
    }
