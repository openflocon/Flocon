package io.github.openflocon.flocondesktop.features.dashboard.data.di

import io.github.openflocon.flocondesktop.features.dashboard.data.DashboardRepositoryImpl
import io.github.openflocon.data.core.dashboard.datasource.DashboardLocalDataSource
import com.flocon.data.remote.dashboard.datasource.ToDeviceDashboardDataSourceImpl
import io.github.openflocon.data.core.dashboard.datasource.DeviceDashboardsDataSource
import io.github.openflocon.data.local.dashboard.datasource.DeviceDashboardsDataSourceInMemory
import io.github.openflocon.data.local.dashboard.datasource.DashboardLocalDataSourceRoom
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
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
    singleOf(::ToDeviceDashboardDataSourceImpl)
    singleOf(::DeviceDashboardsDataSourceInMemory) {
        bind<DeviceDashboardsDataSource>()
    }
}
