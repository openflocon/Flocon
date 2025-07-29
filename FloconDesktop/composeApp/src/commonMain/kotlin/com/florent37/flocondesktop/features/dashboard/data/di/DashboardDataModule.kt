package com.florent37.flocondesktop.features.dashboard.data.di

import com.florent37.flocondesktop.features.dashboard.data.DashboardRepositoryImpl
import com.florent37.flocondesktop.features.dashboard.data.datasources.DashboardLocalDataSource
import com.florent37.flocondesktop.features.dashboard.data.datasources.ToDeviceDashboardDataSource
import com.florent37.flocondesktop.features.dashboard.data.datasources.device.DeviceDashboardsDataSource
import com.florent37.flocondesktop.features.dashboard.data.datasources.device.DeviceDashboardsDataSourceInMemory
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.DashboardLocalDataSourceRoom
import com.florent37.flocondesktop.features.dashboard.domain.repository.DashboardRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dashboardDataModule = module {
    factoryOf(::DashboardRepositoryImpl) {
        bind<DashboardRepository>()
        bind<MessagesReceiverRepository>()
    }
    singleOf(::DashboardLocalDataSourceRoom) {
        bind<DashboardLocalDataSource>()
    }
    singleOf(::ToDeviceDashboardDataSource)
    singleOf(::DeviceDashboardsDataSourceInMemory) {
        bind<DeviceDashboardsDataSource>()
    }
}
