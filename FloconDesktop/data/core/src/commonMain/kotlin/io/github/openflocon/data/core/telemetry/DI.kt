package io.github.openflocon.data.core.telemetry

import io.github.openflocon.data.core.telemetry.repository.TelemetryRepositoryImpl
import io.github.openflocon.domain.telemetry.repository.TelemetryRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val telemetryModule = module {
    singleOf(::TelemetryRepositoryImpl) {
        bind<TelemetryRepository>()
    }
}
