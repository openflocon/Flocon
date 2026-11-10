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

/**
 * Same as above, with groups of events: a group takes one slot of a row, as wide as its marker at [pxPerMs] (see clusterWidthPx).
 * The rows and the clusters of the lane are in parallel: the same index is the same row.
 */
internal fun packLane(
    lane: GraphLane?,
    events: List<GraphEventUiModel>,
    clusters: List<GraphClusterUiModel>,
    pxPerMs: Double,
): GraphLaneUiModel {
    // what is placed: where it starts (the marker of a group starts before its first event) and where its room ends
    class Item(val startMs: Long, val key: String, val fromMs: Double, val toMs: Double, val event: GraphEventUiModel?, val cluster: GraphClusterUiModel?)

    val items = events.map {
        val occupied = if (it.durationMs > 0L) it.durationMs else INSTANT_OCCUPANCY_MS
        Item(it.startMs, it.key, it.startMs.toDouble(), (it.startMs + occupied + GAP_MS).toDouble(), it, null)
    } + clusters.map {
        val fromMs = it.startMs - DiamondSizePx / 2.0 / pxPerMs
        Item(it.startMs, it.key, fromMs, fromMs + (clusterWidthPx(it, pxPerMs) + ClusterGapPx) / pxPerMs, null, it)
    }
    val rowEnds = ArrayList<Double>()
    val rows = ArrayList<MutableList<GraphEventUiModel>>()
    val clusterRows = ArrayList<MutableList<GraphClusterUiModel>>()
    for (item in items.sortedWith(compareBy({ it.startMs }, { it.key }))) {
        var row = rowEnds.indexOfFirst { it <= item.fromMs }
        if (row < 0) {
            row = rowEnds.size
            rowEnds.add(0.0)
            rows.add(mutableListOf())
            clusterRows.add(mutableListOf())
        }
        rowEnds[row] = item.toMs
        item.event?.let { rows[row].add(it) }
        item.cluster?.let { clusterRows[row].add(it) }
    }
    return GraphLaneUiModel(lane = lane, rows = rows, clusters = clusterRows)
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
