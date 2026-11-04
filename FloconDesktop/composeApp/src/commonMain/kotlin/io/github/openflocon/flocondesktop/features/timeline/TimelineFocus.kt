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
    // the session of the graph by start of the app, null: the latest one, which follows the new events
    val graphSession: Long?,
    // the key of the row to select, null: no event
    val selectedKey: String?,
    // null: no session is known, there is no zone to place
    val focus: GraphFocusUiModel?,
)

/**
 * @param sessionStarts the starts of the sessions displayed, oldest first. The session of the request may not be one of them
 * (hidden by a filter): the graph then shows the latest one, and the zone is told since the start of that one.
 */
internal fun planFocus(request: TimelineFocusRequestDomainModel, sessionStarts: List<Long>): TimelineFocusPlan {
    val latest = sessionStarts.lastOrNull()
    val shown = request.sessionStart?.takeIf { it in sessionStarts } ?: latest
    val selectedKey = request.event?.let { "${it.source.name}:${it.id}" }
    return TimelineFocusPlan(
        graphSession = shown?.takeIf { it != latest },
        selectedKey = selectedKey,
        focus = shown?.let {
            GraphFocusUiModel(
                fromMs = request.fromMs - it,
                toMs = request.toMs - it,
                eventKey = selectedKey,
                token = request.requestedAtMs,
            )
        },
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
