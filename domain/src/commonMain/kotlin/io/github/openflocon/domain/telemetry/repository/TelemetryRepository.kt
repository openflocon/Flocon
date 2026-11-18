package io.github.openflocon.domain.telemetry.repository

import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel
import io.github.openflocon.domain.telemetry.models.TelemetryStateDomainModel
import kotlinx.coroutines.flow.StateFlow

interface TelemetryRepository {
    val state: StateFlow<TelemetryStateDomainModel>

    fun setLevel(level: TelemetryLevelDomainModel)

    /** Decides by itself whether the event is sent (key, DO_NOT_TRACK, user level). Never throws. */
    fun track(event: String, properties: Map<String, Any>)
}
