package com.flocon.data.remote.telemetry

import com.flocon.data.remote.telemetry.datasource.PostHogTelemetryRemoteDataSource
import io.github.openflocon.data.core.telemetry.datasource.TelemetryRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val telemetryModule = module {
    singleOf(::PostHogTelemetryRemoteDataSource) bind TelemetryRemoteDataSource::class
}
