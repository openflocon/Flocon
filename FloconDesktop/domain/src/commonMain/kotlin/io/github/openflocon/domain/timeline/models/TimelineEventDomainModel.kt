package io.github.openflocon.domain.timeline.models

import io.github.openflocon.domain.device.models.AppInstance

data class TimelineEventDomainModel(
    val id: String, // the id of the event in the table of its source
    val source: TimelineSource,
    val timestamp: Long,
    val title: String,
    val subtitle: String?,
    val appInstance: AppInstance?, // null when the source does not store it
    val method: String? = null, // network only: GET, POST, WS, GRPC…
    val statusCode: Int? = null, // network only: null when there is no http code (no response yet, gRPC…)
    val durationMs: Double? = null, // network only: null while waiting for the response
    val phase: TimelinePhase = TimelinePhase.Request, // network only: a call has a request event and a response event
    // the start of the session the event belongs to: the latest app start before it, null if none is known
    val sessionStart: AppInstance? = null,
)
