package io.github.openflocon.domain.telemetry.usecase

import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel
import io.github.openflocon.domain.telemetry.repository.TelemetryRepository

class SetTelemetryLevelUseCase(
    private val repository: TelemetryRepository,
) {
    operator fun invoke(level: TelemetryLevelDomainModel) = repository.setLevel(level)
}
