package com.flocon.data.remote.dashboard

import com.flocon.data.remote.dashboard.datasource.ToDeviceDashboardDataSourceImpl
import io.github.openflocon.data.core.dashboard.datasource.ToDeviceDashboardDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val dashboardModule = module {
    singleOf(::ToDeviceDashboardDataSourceImpl) bind ToDeviceDashboardDataSource::class
}
