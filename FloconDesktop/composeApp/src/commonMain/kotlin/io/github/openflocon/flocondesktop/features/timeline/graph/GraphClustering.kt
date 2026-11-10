package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.runtime.Immutable
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow

// the events closer than this on screen are one marker
internal const val ClusterDistancePx = 14f

// the width of a marker with one source, and what each other source adds
internal const val ClusterMinWidthPx = 48f
internal const val ClusterSourceWidthPx = 8f

// the room kept between a marker and the next one of its row
internal const val ClusterGapPx = 4f

// the smallest share of the marker given to a source, so a source with one event among 99 can still be seen
internal const val ClusterMinSegmentRatio = 0.08f

// the zoom is cut in steps of 15 %: the markers are computed again only past one
private const val ZoomBucketStep = 1.15

/**
 * Events too close to be told apart on screen, drawn as one marker.
 * [counts] gives the number of events per source, in the order of [GraphLane], the sources without event are left out.
 */
@Immutable
internal data class GraphClusterUiModel(
    // sorted by start, at least two
    val events: List<GraphEventUiModel>,
    val counts: List<Pair<GraphLane, Int>>,
) {
    val key: String = "cluster:" + events.first().key
    val startMs: Long = events.first().startMs
    val endMs: Long = events.maxOf { it.endMs }

    // the start of the last one: the marker spans the starts, the bars it holds are not drawn
    val lastStartMs: Long = events.last().startMs
    val errorCount: Int = events.count { it.isError }
    val size: Int get() = events.size
}

private fun clusterOf(events: List<GraphEventUiModel>): GraphClusterUiModel {
    val byLane = events.groupingBy { it.lane }.eachCount()
    return GraphClusterUiModel(
        events = events,
        counts = GraphLane.entries.mapNotNull { lane -> byLane[lane]?.let { lane to it } },
    )
}

internal data class ClusteredEvents(
    val singles: List<GraphEventUiModel>,
    val clusters: List<GraphClusterUiModel>,
)

/**
 * Groups the events that start less than [ClusterDistancePx] from the first one of their group, at [pxPerMs].
 * Only the start counts: a burst of calls that start together is one marker, whatever their duration. A group of one stays an event.
 * Pure: sorted by (start, key), so the same events always give the same groups.
 */
internal fun clusterEvents(events: List<GraphEventUiModel>, pxPerMs: Double): ClusteredEvents {
    val singles = ArrayList<GraphEventUiModel>()
    val clusters = ArrayList<GraphClusterUiModel>()
    var group = ArrayList<GraphEventUiModel>()
    fun flush() {
        if (group.size == 1) {
            singles.add(group[0])
        } else if (group.size > 1) {
            clusters.add(clusterOf(group))
        }
        group = ArrayList()
    }
    val maxDistanceMs = ClusterDistancePx / pxPerMs
    for (event in events.sortedWith(compareBy({ it.startMs }, { it.key }))) {
        if (group.isNotEmpty() && event.startMs - group.first().startMs > maxDistanceMs) flush()
        group.add(event)
    }
    flush()
    return ClusteredEvents(singles, clusters)
}

// the width of the marker of a cluster: enough for its sources, and for the time its events start over
internal fun clusterWidthPx(cluster: GraphClusterUiModel, pxPerMs: Double): Float = max(
    ClusterMinWidthPx + ClusterSourceWidthPx * (cluster.counts.size - 1),
    ((cluster.lastStartMs - cluster.startMs) * pxPerMs).toFloat() + DiamondSizePx,
)

// the marker is centered on its first event, like a diamond
internal fun GraphViewport.clusterLeftPx(cluster: GraphClusterUiModel): Float = xOf(cluster.startMs) - DiamondSizePx / 2f

internal data class ClusterSegment(val lane: GraphLane, val count: Int, val widthPx: Float)

/**
 * The parts of a marker of [widthPx], one per source, in the order of the lanes. A part is as wide as its share of the
 * events, but never under [ClusterMinSegmentRatio] of the marker: the others share what is left.
 */
internal fun clusterSegments(cluster: GraphClusterUiModel, widthPx: Float): List<ClusterSegment> {
    val counts = cluster.counts
    val ratios = DoubleArray(counts.size)
    val fixed = BooleanArray(counts.size)
    while (true) {
        val freeCount = counts.indices.filter { !fixed[it] }.sumOf { counts[it].second }
        val freeRatio = 1.0 - ClusterMinSegmentRatio * fixed.count { it }
        var changed = false
        for (i in counts.indices) {
            if (fixed[i]) {
                ratios[i] = ClusterMinSegmentRatio.toDouble()
                continue
            }
            ratios[i] = counts[i].second.toDouble() / freeCount * freeRatio
        }
        // the smallest one under the minimum is fixed to it, then the others are shared again
        val smallest = counts.indices.filter { !fixed[it] && ratios[it] < ClusterMinSegmentRatio }.minByOrNull { ratios[it] }
        if (smallest != null) {
            fixed[smallest] = true
            changed = true
        }
        if (!changed) break
    }
    return counts.mapIndexed { index, (lane, count) -> ClusterSegment(lane, count, (ratios[index] * widthPx).toFloat()) }
}

/** The zoom bucket of [pxPerMs]: the markers are the same inside one. */
internal fun bucketOf(pxPerMs: Double): Int = floor(ln(pxPerMs) / ln(ZoomBucketStep)).toInt()

// the lower edge of the bucket: a marker is a little too big rather than too small
internal fun bucketPxPerMs(bucket: Int): Double = ZoomBucketStep.pow(bucket)

/**
 * The graph with the close events grouped, for the zoom [pxPerMs]: each lane is packed again, a group taking one slot of a row.
 * A lane without group is left as it is.
 */
internal fun GraphUiModel.clustered(pxPerMs: Double): GraphUiModel = copy(
    lanes = lanes.map { lane ->
        val clustered = clusterEvents(lane.rows.flatten() + lane.clusters.flatten().flatMap { it.events }, pxPerMs)
        if (clustered.clusters.isEmpty()) lane else packLane(lane.lane, clustered.singles, clustered.clusters, pxPerMs)
    },
)

// the group with this key, if the graph still has it
internal fun GraphUiModel.clusterByKey(key: String?): GraphClusterUiModel? {
    key ?: return null
    return lanes.firstNotNullOfOrNull { lane -> lane.clusters.firstNotNullOfOrNull { row -> row.firstOrNull { it.key == key } } }
}
