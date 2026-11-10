package io.github.openflocon.flocondesktop.features.timeline.graph

// the margin kept after each event placed, in ms
internal const val GAP_MS = 15L

// the room reserved to an instant event, so its label does not cover the next one. In ms: it does not depend on the zoom
internal const val INSTANT_OCCUPANCY_MS = 320L

/**
 * Places the events of a lane on the first row where they do not overlap the previous ones.
 * Pure: sorted by (start, key), so the same events always give the same rows.
 */
internal fun packLane(lane: GraphLane?, events: List<GraphEventUiModel>): GraphLaneUiModel {
    val rowEnds = ArrayList<Long>()
    val rows = ArrayList<MutableList<GraphEventUiModel>>()
    for (event in events.sortedWith(compareBy({ it.startMs }, { it.key }))) {
        val occupied = if (event.durationMs > 0L) event.durationMs else INSTANT_OCCUPANCY_MS
        var row = rowEnds.indexOfFirst { it <= event.startMs }
        if (row < 0) {
            row = rowEnds.size
            rowEnds.add(0L)
            rows.add(mutableListOf())
        }
        rowEnds[row] = event.startMs + occupied + GAP_MS
        rows[row].add(event)
    }
    return GraphLaneUiModel(lane = lane, rows = rows)
}

// the lanes in their order, the ones without event are hidden
internal fun packLanes(events: List<GraphEventUiModel>): List<GraphLaneUiModel> {
    val byLane = events.groupBy { it.lane }
    return GraphLane.entries.mapNotNull { lane -> byLane[lane]?.let { packLane(lane, it) } }
}

// every type in one lane: the rows are shared, a type is told by its color
internal fun packSingleLane(events: List<GraphEventUiModel>): List<GraphLaneUiModel> = if (events.isEmpty()) emptyList() else listOf(packLane(null, events))

internal fun buildGraphUiModel(
    events: List<GraphEventUiModel>,
    sessionDurationMs: Long,
    sessionStartTimestamp: Long = 0L,
    hasAppStart: Boolean = false,
    single: Boolean = false,
): GraphUiModel {
    val completed = events.filter { it.lane == GraphLane.Network && it.durationMs > 0L }
    return GraphUiModel(
        lanes = if (single) packSingleLane(events) else packLanes(events),
        sessionDurationMs = sessionDurationMs,
        sessionStartTimestamp = sessionStartTimestamp,
        hasAppStart = hasAppStart,
        eventCount = events.size,
        errorCount = events.count { it.isError },
        slowest = completed.maxByOrNull { it.durationMs },
    )
}
