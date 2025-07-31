package io.github.openflocon.flocondesktop.features.table.data.di

import com.florent37.flocondesktop.features.table.data.TableRepositoryImpl
import com.florent37.flocondesktop.features.table.data.datasource.RemoteTableDataSource
import com.florent37.flocondesktop.features.table.data.datasource.device.DeviceTablesDataSource
import com.florent37.flocondesktop.features.table.data.datasource.device.DeviceTablesDataSourceInMemory
import com.florent37.flocondesktop.features.table.data.datasource.local.TableLocalDataSource
import com.florent37.flocondesktop.features.table.data.datasource.local.TableLocalDataSourceRoom
import com.florent37.flocondesktop.features.table.domain.repository.TableRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val tableDataModule =
    module {
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
        singleOf(::RemoteTableDataSource)
    }
