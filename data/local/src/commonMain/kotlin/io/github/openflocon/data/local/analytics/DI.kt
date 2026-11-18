package io.github.openflocon.data.local.analytics

import io.github.openflocon.data.core.analytics.datasource.AnalyticsLocalDataSource
import io.github.openflocon.data.core.analytics.datasource.DeviceAnalyticsDataSource
import io.github.openflocon.data.local.analytics.datasource.AnalyticsLocalDataSourceRoom
import io.github.openflocon.data.local.analytics.datasource.DeviceAnalyticsDataSourceInMemory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val analyticsModule = module {
    singleOf(::AnalyticsLocalDataSourceRoom) bind AnalyticsLocalDataSource::class
    singleOf(::DeviceAnalyticsDataSourceInMemory) bind DeviceAnalyticsDataSource::class
}
