package io.github.openflocon.data.core.telemetry.datasource

import io.github.openflocon.domain.telemetry.models.TelemetryConfigDomainModel

interface TelemetryRemoteDataSource {
    /** Sends a single event. Must never throw: telemetry can't break the app. */
    suspend fun capture(
        config: TelemetryConfigDomainModel,
        event: String,
        distinctId: String,
        properties: Map<String, Any>,
    )
}
