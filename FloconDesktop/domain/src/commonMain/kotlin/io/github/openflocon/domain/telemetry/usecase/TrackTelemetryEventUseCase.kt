package io.github.openflocon.domain.telemetry.usecase

import io.github.openflocon.domain.telemetry.repository.TelemetryRepository

/**
 * Only call it with feature-level information (screen names, version, OS).
 * Never pass anything coming from the inspected apps: URLs, payloads, package names, database content...
 */
class TrackTelemetryEventUseCase(
    private val repository: TelemetryRepository,
) {
    operator fun invoke(event: String, properties: Map<String, Any> = emptyMap()) = repository.track(event, properties)
}
