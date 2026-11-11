package io.github.openflocon.domain.telemetry

import io.github.openflocon.domain.telemetry.usecase.ObserveTelemetryStateUseCase
import io.github.openflocon.domain.telemetry.usecase.SetTelemetryLevelUseCase
import io.github.openflocon.domain.telemetry.usecase.TrackTelemetryEventUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val telemetryDomainModule = module {
    factoryOf(::ObserveTelemetryStateUseCase)
    factoryOf(::SetTelemetryLevelUseCase)
    factoryOf(::TrackTelemetryEventUseCase)
}
