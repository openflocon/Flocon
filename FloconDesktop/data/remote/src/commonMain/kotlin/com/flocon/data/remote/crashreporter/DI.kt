package com.flocon.data.remote.crashreporter

import com.flocon.data.remote.analytics.datasource.AnalyticsRemoteDataSourceImpl
import com.flocon.data.remote.crashreporter.datasource.CrashReportRemoteDataSourceImpl
import io.github.openflocon.data.core.analytics.datasource.AnalyticsRemoteDataSource
import io.github.openflocon.data.core.crashreporter.datasources.CrashReportRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val crashReportModule = module {
    singleOf(::CrashReportRemoteDataSourceImpl) bind CrashReportRemoteDataSource::class
}
