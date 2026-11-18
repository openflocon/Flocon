package io.github.openflocon.domain.telemetry.usecase

import io.github.openflocon.domain.telemetry.models.TelemetryStateDomainModel
import io.github.openflocon.domain.telemetry.repository.TelemetryRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveTelemetryStateUseCase(
    private val repository: TelemetryRepository,
) {
    operator fun invoke(): StateFlow<TelemetryStateDomainModel> = repository.state
}
