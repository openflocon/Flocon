package com.flocon.data.remote.analytics

import com.flocon.data.remote.analytics.datasource.AnalyticsRemoteDataSourceImpl
import io.github.openflocon.data.core.analytics.datasource.AnalyticsRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val analyticsModule = module {
    singleOf(::AnalyticsRemoteDataSourceImpl) bind AnalyticsRemoteDataSource::class
}
