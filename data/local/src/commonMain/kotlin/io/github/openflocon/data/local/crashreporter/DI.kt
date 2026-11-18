package io.github.openflocon.data.local.crashreporter

import io.github.openflocon.data.core.crashreporter.datasources.CrashReporterLocalDataSource
import io.github.openflocon.data.local.crashreporter.datasource.CrashReporterLocalDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val crashReporterLocalModule = module {
    singleOf(::CrashReporterLocalDataSourceRoom) bind CrashReporterLocalDataSource::class
}
