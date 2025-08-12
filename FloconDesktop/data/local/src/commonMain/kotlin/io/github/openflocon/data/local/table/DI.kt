package io.github.openflocon.data.local.table

import io.github.openflocon.data.core.table.datasource.DeviceTablesDataSource
import io.github.openflocon.data.core.table.datasource.TableLocalDataSource
import io.github.openflocon.data.local.table.datasource.DeviceTablesDataSourceInMemory
import io.github.openflocon.data.local.table.datasource.TableLocalDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val tableModule = module {
    singleOf(::DeviceTablesDataSourceInMemory) bind DeviceTablesDataSource::class
    singleOf(::TableLocalDataSourceRoom) bind TableLocalDataSource::class
}
