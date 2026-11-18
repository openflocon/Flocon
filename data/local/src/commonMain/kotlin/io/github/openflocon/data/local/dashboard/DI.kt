package io.github.openflocon.data.local.dashboard

import io.github.openflocon.data.core.dashboard.datasource.DashboardLocalDataSource
import io.github.openflocon.data.core.dashboard.datasource.DeviceDashboardsDataSource
import io.github.openflocon.data.local.dashboard.datasource.DashboardLocalDataSourceRoom
import io.github.openflocon.data.local.dashboard.datasource.DeviceDashboardsDataSourceInMemory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val dashboardModule = module {
    singleOf(::DashboardLocalDataSourceRoom) bind DashboardLocalDataSource::class
    singleOf(::DeviceDashboardsDataSourceInMemory) bind DeviceDashboardsDataSource::class
}
