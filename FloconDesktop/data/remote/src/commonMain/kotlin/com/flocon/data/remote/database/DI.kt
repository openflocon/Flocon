package com.flocon.data.remote.database

import com.flocon.data.remote.database.datasource.DeviceDatabasesRemoteDataSourceImpl
import com.flocon.data.remote.database.datasource.QueryDatabaseRemoteDataSourceImpl
import io.github.openflocon.data.core.database.datasource.DeviceDatabasesRemoteDataSource
import io.github.openflocon.data.core.database.datasource.QueryDatabaseRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val databaseModule = module {
    singleOf(::DeviceDatabasesRemoteDataSourceImpl) bind DeviceDatabasesRemoteDataSource::class
    singleOf(::QueryDatabaseRemoteDataSourceImpl) bind QueryDatabaseRemoteDataSource::class
}
