package io.github.openflocon.flocondesktop.features.database.data.di

import com.flocon.data.remote.database.datasource.DeviceDatabasesRemoteDataSourceImpl
import com.flocon.data.remote.database.datasource.QueryDatabaseRemoteDataSourceImpl
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.flocondesktop.features.database.data.DatabaseRepositoryImpl
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
    }
