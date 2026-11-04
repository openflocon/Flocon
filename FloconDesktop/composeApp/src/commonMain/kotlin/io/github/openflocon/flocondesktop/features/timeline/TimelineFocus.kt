package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFocusRequestDomainModel
import io.github.openflocon.flocondesktop.features.timeline.graph.GraphFocusUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

/**
 * What the timeline does to show a [TimelineFocusRequestDomainModel].
 */
internal data class TimelineFocusPlan(
    // the key of the row to select, null: no event
    val selectedKey: String?,
    // null: the graph is not displayed (list view), it is left as the user had it
    val graph: GraphFocusPlan?,
)

internal data class GraphFocusPlan(
    // the session of the graph by start of the app, null: the latest one, which follows the new events
    val session: Long?,
    // null: no session is known, there is no zone to place
    val focus: GraphFocusUiModel?,
)

/**
 * @param sessionStarts the starts of the sessions displayed, oldest first. The session of the request may not be one of them
 * (hidden by a filter): the graph then shows the latest one, and the zone is told since the start of that one.
 * @param graphShown false in list view: the graph keeps its session and no zone waits for it, or it would jump there the day the graph is turned on
 */
internal fun planFocus(request: TimelineFocusRequestDomainModel, sessionStarts: List<Long>, graphShown: Boolean): TimelineFocusPlan {
    val selectedKey = request.event?.let { "${it.source.name}:${it.id}" }
    if (!graphShown) return TimelineFocusPlan(selectedKey = selectedKey, graph = null)

    val latest = sessionStarts.lastOrNull()
    val shown = request.sessionStart?.takeIf { it in sessionStarts } ?: latest
    return TimelineFocusPlan(
        selectedKey = selectedKey,
        graph = GraphFocusPlan(
            session = shown?.takeIf { it != latest },
            focus = shown?.let {
                GraphFocusUiModel(
                    fromMs = request.fromMs - it,
                    toMs = request.toMs - it,
                    eventKey = selectedKey,
                    token = request.requestedAtMs,
                )
            },
        ),
    )
}

/**
 * The requests to apply: each one waits for its device and app to be the ones displayed (the selection is changed
 * in the same moment as the request, and lands after it). A new request replaces the one that is waiting.
 */
internal fun appliedFocusRequests(
    requests: Flow<TimelineFocusRequestDomainModel>,
    current: Flow<DeviceIdAndPackageNameDomainModel?>,
): Flow<TimelineFocusRequestDomainModel> = requests.flatMapLatest { request ->
    current
        .filter { it != null && it.deviceId == request.target.deviceId && it.packageName == request.target.packageName }
        .take(1)
        .map { request }
}
