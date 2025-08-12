package io.github.openflocon.data.local.database

import io.github.openflocon.data.core.database.datasource.LocalDatabaseDataSource
import io.github.openflocon.data.local.database.datasource.LocalDatabaseDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val databaseModule = module {
    singleOf(::LocalDatabaseDataSourceRoom) bind LocalDatabaseDataSource::class
}
