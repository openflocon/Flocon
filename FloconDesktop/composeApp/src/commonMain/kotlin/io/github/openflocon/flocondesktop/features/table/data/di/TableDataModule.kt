package io.github.openflocon.flocondesktop.features.table.data.di

import io.github.openflocon.domain.table.repository.TableRepository
import io.github.openflocon.flocondesktop.features.table.data.TableRepositoryImpl
import io.github.openflocon.flocondesktop.features.table.data.datasource.device.DeviceTablesDataSource
import io.github.openflocon.flocondesktop.features.table.data.datasource.device.DeviceTablesDataSourceInMemory
import io.github.openflocon.data.core.table.datasource.TableLocalDataSource
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.TableLocalDataSourceRoom
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val tableDataModule = module {
    singleOf(::TableRepositoryImpl) {
        bind<TableRepository>()
        bind<MessagesReceiverRepository>()
    }
    singleOf(::TableLocalDataSourceRoom) {
        bind<TableLocalDataSource>()
    }
    singleOf(::DeviceTablesDataSourceInMemory) {
        bind<DeviceTablesDataSource>()
    }
}
