package io.github.openflocon.flocondesktop.features.timeline.graph

import kotlin.math.max
import kotlin.math.roundToInt

// geometry in px, see the design of the graph
internal const val LaneLabelWidthPx = 130f
internal const val AxisHeightPx = 40f
internal const val RowHeightPx = 40f
internal const val BarTopPx = 6f
internal const val BarHeightPx = 28f
internal const val MinBarWidthPx = 8f
internal const val DiamondSizePx = 12f
internal const val TrackEndMarginPx = 24f
internal const val LaneSeparatorPx = 1f

private const val IconMinWidthPx = 30f
private const val ChipMinWidthPx = 72f
private const val TextMinWidthPx = 36f
private const val HitTolerancePx = 4f

// what a bar of [widthPx] has room for
internal fun showBarIcon(widthPx: Float) = widthPx >= IconMinWidthPx

internal fun showBarChip(widthPx: Float) = widthPx >= ChipMinWidthPx

// [remainingPx]: the width left in the bar once the icon and the chip are placed
internal fun showBarText(remainingPx: Float) = remainingPx >= TextMinWidthPx

/**
 * What the viewport of the graph shows: the time at the left of the track, the zoom and the vertical scroll.
 */
internal data class GraphViewport(
    val startMs: Double = 0.0,
    val pxPerMs: Double = 0.1,
    val scrollY: Float = 0f,
)

internal fun GraphViewport.xOf(ms: Long): Float = ((ms - startMs) * pxPerMs).toFloat()

internal fun GraphViewport.msAt(x: Float): Double = startMs + x / pxPerMs

// the graph when it opens: 100 %, a second before the start of the session to see the app start
internal fun defaultViewport(trackWidthPx: Float) = GraphViewport(startMs = MIN_START_MS, pxPerMs = (trackWidthPx / ZOOM_REFERENCE_MS).coerceAtLeast(MIN_PX_PER_MS))

// the whole session in [trackWidthPx]
internal fun fitViewport(sessionDurationMs: Long, trackWidthPx: Float): GraphViewport {
    val duration = max(sessionDurationMs, 1L)
    return GraphViewport(startMs = 0.0, pxPerMs = (trackWidthPx / duration).toDouble().coerceAtLeast(MIN_PX_PER_MS))
}

// the most the graph shows before the start of the session, the app start is at 0
internal const val MIN_START_MS = -1_000.0
internal const val MIN_PX_PER_MS = 0.0005
internal const val MAX_PX_PER_MS = 20.0

/**
 * The zoom in percent: 100 when the track shows 30 s, 200 when it shows 15 s.
 */
internal fun GraphViewport.zoomPercent(trackWidthPx: Float): Int = (pxPerMs / (trackWidthPx / ZOOM_REFERENCE_MS) * 100.0).roundToInt()

// zooms to [percent] around the middle of the track, see zoomPercent
internal fun GraphViewport.zoomedToPercent(percent: Int, trackWidthPx: Float): GraphViewport {
    if (percent <= 0) return this
    val target = percent / 100.0 * (trackWidthPx / ZOOM_REFERENCE_MS)
    return zoomed(target / pxPerMs, trackWidthPx / 2)
}

// the duration the track shows at 100 %
internal const val ZOOM_REFERENCE_MS = 30_000.0

// zooms around the point [anchorX] of the track, which stays under the pointer
internal fun GraphViewport.zoomed(factor: Double, anchorX: Float): GraphViewport {
    val newPx = (pxPerMs * factor).coerceIn(MIN_PX_PER_MS, MAX_PX_PER_MS)
    val anchorMs = msAt(anchorX)
    return copy(pxPerMs = newPx, startMs = (anchorMs - anchorX / newPx).coerceAtLeast(MIN_START_MS))
}

/**
 * Shows the time between two x of the track ([fromX] and [toX], in any order) in the whole track.
 * A box too narrow to be a zoom is ignored.
 */
internal fun GraphViewport.zoomedToRange(fromX: Float, toX: Float, trackWidthPx: Float): GraphViewport {
    val left = minOf(fromX, toX).coerceIn(0f, trackWidthPx)
    val right = maxOf(fromX, toX).coerceIn(0f, trackWidthPx)
    if (right - left < MinZoomBoxPx) return this
    val startMs = msAt(left).coerceAtLeast(MIN_START_MS)
    val endMs = msAt(right)
    val newPx = (trackWidthPx / (endMs - startMs)).coerceIn(MIN_PX_PER_MS, MAX_PX_PER_MS)
    return copy(startMs = startMs, pxPerMs = newPx)
}

private const val MinZoomBoxPx = 8f

// moves along the time axis, never more than a second before the start of the session
internal fun GraphViewport.panned(deltaMs: Double): GraphViewport = copy(startMs = (startMs + deltaMs).coerceAtLeast(MIN_START_MS))

// the height of a lane: its rows
internal fun GraphLaneUiModel.heightPx(): Float = rows.size * RowHeightPx

// the top of each lane, from the first one, and the total height
internal fun GraphUiModel.laneTops(): FloatArray {
    val tops = FloatArray(lanes.size)
    var y = 0f
    lanes.forEachIndexed { index, lane ->
        tops[index] = y
        y += lane.heightPx() + LaneSeparatorPx
    }
    return tops
}

internal fun GraphUiModel.contentHeightPx(): Float = lanes.sumOf { (it.heightPx() + LaneSeparatorPx).toDouble() }.toFloat()

/**
 * The index of the first event of a row that ends at or after [ms]: the events of a row do not overlap, so they are
 * sorted by end too. [row].size when none.
 */
internal fun firstVisibleIndex(row: List<GraphEventUiModel>, ms: Double): Int {
    var low = 0
    var high = row.size
    while (low < high) {
        val mid = (low + high) ushr 1
        if (row[mid].endMs < ms) low = mid + 1 else high = mid
    }
    return low
}

/**
 * The event under a point of the track: [yInContent] is the position in the lanes (scroll included), [x] the one in the
 * track. A bar or a diamond is hit with a margin of a few px.
 */
internal fun GraphUiModel.hitTest(viewport: GraphViewport, x: Float, yInContent: Float): GraphEventUiModel? {
    val tops = laneTops()
    val laneIndex = tops.indexOfLast { it <= yInContent }.takeIf { it >= 0 } ?: return null
    val lane = lanes[laneIndex]
    val rowIndex = ((yInContent - tops[laneIndex]) / RowHeightPx).toInt()
    val row = lane.rows.getOrNull(rowIndex) ?: return null
    val ms = viewport.msAt(x)
    val toleranceMs = HitTolerancePx / viewport.pxPerMs
    // the diamond is centered on its time, the bar starts at it
    val halfInstantMs = DiamondSizePx / 2 / viewport.pxPerMs
    val minWidthMs = MinBarWidthPx / viewport.pxPerMs
    // an instant can be hit by its label, on the right of it: look back as far as a label goes
    val labelMs = (InstantLabelOffsetPx + MaxLabelHitPx) / viewport.pxPerMs
    var i = firstVisibleIndex(row, ms - toleranceMs - max(max(halfInstantMs, minWidthMs), labelMs))
    while (i < row.size) {
        val event = row[i]
        val (from, to) = if (event.isInstant) {
            event.startMs - halfInstantMs to event.startMs + halfInstantMs
        } else {
            event.startMs.toDouble() to max(event.endMs.toDouble(), event.startMs + minWidthMs)
        }
        if (from - toleranceMs > ms) return null
        // the label of an instant is part of it, up to the next event
        val labelEndMs = if (event.isInstant) {
            val room = instantLabelRoomPx(event, row.getOrNull(i + 1), viewport.pxPerMs, onTheLeft = false)
            event.startMs + (InstantLabelOffsetPx + (room?.coerceAtLeast(0f)?.let { minOf(it, event.labelWidthPx()) } ?: event.labelWidthPx())) / viewport.pxPerMs
        } else {
            to
        }
        if (ms in (from - toleranceMs)..(max(to, labelEndMs) + toleranceMs)) return event
        i++
    }
    return null
}

// the most recent event: the one that ends last, the latest started when several end together
internal fun GraphUiModel.latestEvent(): GraphEventUiModel? = lanes
    .flatMap { lane -> lane.rows.map { row -> row.lastOrNull() } }
    .filterNotNull()
    .maxWithOrNull(compareBy({ it.endMs }, { it.startMs }))

// the top of the row of an event in the lanes
internal fun GraphUiModel.rowTopOf(event: GraphEventUiModel): Float? {
    val tops = laneTops()
    lanes.forEachIndexed { laneIndex, lane ->
        val rowIndex = lane.rows.indexOfFirst { row -> row.any { it.key == event.key } }
        if (rowIndex >= 0) return tops[laneIndex] + rowIndex * RowHeightPx
    }
    return null
}

/**
 * Moves to the most recent event, at the right of the track, in the row that holds it. The zoom does not change.
 */
internal fun GraphViewport.jumpedToLatest(model: GraphUiModel, trackWidthPx: Float, trackHeightPx: Float): GraphViewport {
    val event = model.latestEvent() ?: return this
    val rowTop = model.rowTopOf(event) ?: 0f
    val maxScroll = max(model.contentHeightPx() - trackHeightPx, 0f)
    return copy(
        startMs = max(event.endMs - (trackWidthPx - JumpMarginPx) / pxPerMs, MIN_START_MS),
        scrollY = (rowTop - (trackHeightPx - RowHeightPx) / 2f).coerceIn(0f, maxScroll),
    )
}

private const val JumpMarginPx = 80f

/**
 * The width the label of an instant can take before it meets the [neighbour] of its row: the next event when the label
 * is on the right of the diamond, the previous one when [onTheLeft]. Null: all the room it needs.
 */
internal fun instantLabelRoomPx(event: GraphEventUiModel, neighbour: GraphEventUiModel?, pxPerMs: Double, onTheLeft: Boolean): Float? {
    neighbour ?: return null
    val gapMs = if (onTheLeft) event.startMs - neighbour.endMs else neighbour.startMs - event.startMs
    // the label starts after the diamond and stops before the diamond or the bar of the neighbour
    return (gapMs * pxPerMs).toFloat() - InstantLabelOffsetPx - InstantLabelGapPx - DiamondSizePx / 2f
}

internal const val InstantLabelOffsetPx = 14f
internal const val InstantLabelGapPx = 6f
private const val InstantCharWidthPx = 6.5f

// about the width of the label of an instant, to hit it with the pointer
internal fun GraphEventUiModel.labelWidthPx(): Float = minOf(name.length * InstantCharWidthPx, MaxLabelHitPx)

private const val MaxLabelHitPx = 600f
