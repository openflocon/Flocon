package io.github.openflocon.flocondesktop.features.timeline.graph

import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.mapper.splitUrl
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkUiModel
import kotlin.math.roundToLong

/**
 * The graph of one session, T0 is [sessionStart] (the start of the app).
 *
 * A network call is a request and a response in the timeline: one bar here, from the request, as long as the response
 * took. A WebSocket message is an instant. A call without response (pending, or a stream that never ends) is not drawn: its bar would have no end. The database and analytics events have no
 * duration in the protocol: they are instants, like the logs. The other sources are not drawn.
 */
internal fun buildGraph(events: List<TimelineEventDomainModel>, sessionStart: Long, single: Boolean = false): GraphUiModel {
    val requestIds = events
        .filter { it.source == TimelineSource.Network && it.phase == TimelinePhase.Request }
        .mapTo(HashSet()) { it.id }

    val hasAppStart = events.any { it.source == TimelineSource.AppStart }
    val mapped = events.mapNotNull { event ->
        val lane = event.source.toGraphLane() ?: return@mapNotNull null
        when (event.source) {
            TimelineSource.Network -> {
                // a response alone (its request is filtered out) starts as long before as it took
                if (event.phase == TimelinePhase.Response && event.id in requestIds) return@mapNotNull null
                // a WebSocket message happens at a time: no response to wait for, so no duration
                val instant = event.method == WEB_SOCKET_METHOD
                val duration = (if (instant) 0L else event.durationMs?.roundToLong()) ?: return@mapNotNull null
                val start = if (event.phase == TimelinePhase.Response) event.timestamp - duration else event.timestamp
                val ui = event.toUi()
                GraphEventUiModel(
                    row = ui,
                    lane = lane,
                    startMs = (start - sessionStart).coerceAtLeast(0L),
                    durationMs = duration,
                    name = ui.network?.graphName() ?: splitUrl(event.title).second.substringBefore('?'),
                    method = event.method?.takeIf { it.isNotEmpty() },
                    statusCode = event.statusCode,
                    isImage = event.isImage,
                    hasError = event.hasError,
                )
            }

            else -> GraphEventUiModel(
                row = event.toUi(),
                lane = lane,
                startMs = (event.timestamp - sessionStart).coerceAtLeast(0L),
                durationMs = 0L,
                name = event.title,
                // a log of level error: computed in the query
                hasError = event.hasError,
                logLevel = event.level.takeIf { event.source == TimelineSource.Log },
                logType = event.subtitle.takeIf { event.source == TimelineSource.Log },
            )
        }
    }
    val sessionDuration = mapped.maxOfOrNull { it.endMs } ?: 0L
    return buildGraphUiModel(mapped, sessionDuration, sessionStartTimestamp = sessionStart, hasAppStart = hasAppStart, single = single)
}

private const val WEB_SOCKET_METHOD = "WS"

// a gRPC call is told by its method, the host and the service are the same for most calls
private fun TimelineNetworkUiModel.graphName(): String = if (method == "GRPC") {
    path.substringAfterLast('/')
} else {
    path.substringBefore('?')
}
