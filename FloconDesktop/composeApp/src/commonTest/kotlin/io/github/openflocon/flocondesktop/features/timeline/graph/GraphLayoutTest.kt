package io.github.openflocon.flocondesktop.features.timeline.graph

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GraphLayoutTest {

    private val model = buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs)

    // 1 px per ms from 0
    private val viewport = GraphViewport(startMs = 0.0, pxPerMs = 1.0)

    private fun row(vararg bars: Pair<Long, Long>) = bars.mapIndexed { i, (start, duration) ->
        previewGraphEvents().first { it.row.id == "1" }.let {
            it.copy(row = it.row.copy(id = "$i"), startMs = start, durationMs = duration)
        }
    }

    @Test
    fun `the first visible bar`() {
        val bars = row(0L to 100L, 200L to 100L, 400L to 100L)

        assertEquals(0, firstVisibleIndex(bars, 0.0))
        assertEquals(0, firstVisibleIndex(bars, 100.0)) // ends exactly there
        assertEquals(1, firstVisibleIndex(bars, 150.0))
        assertEquals(1, firstVisibleIndex(bars, 250.0)) // straddles the left edge
        assertEquals(2, firstVisibleIndex(bars, 350.0))
        assertEquals(3, firstVisibleIndex(bars, 501.0))
        assertEquals(0, firstVisibleIndex(emptyList(), 10.0))
    }

    @Test
    fun `hit test finds a bar`() {
        // the network lane is the second one, after analytics: the feed is on its second row
        val networkTop = model.laneTops()[1]
        val feed = model.hitTest(viewport, x = 400f, yInContent = networkTop + RowHeightPx + 10f)

        assertEquals("3", feed?.row?.id)
    }

    @Test
    fun `hit test finds a diamond with a tolerance`() {
        val analyticsTop = model.laneTops().first()
        val appOpen = model.hitTest(viewport, x = 20f + 9f, yInContent = analyticsTop + 10f) // 6 px of diamond and 4 of margin

        assertEquals("2", appOpen?.row?.id)
    }

    @Test
    fun `hit test finds nothing in the empty parts`() {
        val analyticsTop = model.laneTops().first()

        assertNull(model.hitTest(viewport, x = 500f, yInContent = analyticsTop + 10f))
        assertNull(model.hitTest(viewport, x = 500f, yInContent = 10_000f))
        assertNull(model.hitTest(viewport, x = 500f, yInContent = -5f))
    }

    @Test
    fun `zoom keeps the point under the pointer`() {
        val zoomed = viewport.copy(startMs = 100.0).zoomed(2.0, anchorX = 50f)

        assertEquals(150.0, zoomed.msAt(50f), 0.001)
        assertEquals(2.0, zoomed.pxPerMs, 0.001)
    }

    @Test
    fun `fit shows the whole session`() {
        val fit = fitViewport(3_000L, 600f)

        assertEquals(0.2, fit.pxPerMs, 0.0001)
        assertTrue(fit.xOf(3_000L) <= 600f + 0.001f)
    }

    @Test
    fun `tooltip of a call`() {
        val feed = previewGraphEvents().first { it.row.id == "3" }

        assertEquals("GET /v1/feed - +190 ms - 640 ms - 200", feed.tooltip())
    }

    @Test
    fun `jump to the latest event`() {
        val jumped = GraphViewport(startMs = 0.0, pxPerMs = 1.0).jumpedToLatest(model, trackWidthPx = 600f, trackHeightPx = 100f)

        // the analytics event at 2830 ms ends last: it is at the right of the track, with a margin. Analytics is the top lane: no vertical scroll
        assertEquals("15", model.latestEvent()?.row?.id)
        assertEquals(2830.0 - 520.0, jumped.startMs, 0.001)
        assertEquals(0f, jumped.scrollY)
    }

    @Test
    fun `the viewport cannot be panned past the limit`() {
        // a limit at 2830 ms: at the right of a 600 px track, with the 80 px margin, the start is 2310 ms
        assertEquals(2_310.0, viewport.copy(startMs = 2_000.0).panned(5_000.0, 2_830L, 600f).startMs, 0.001)
        assertEquals(2_100.0, viewport.copy(startMs = 2_000.0).panned(100.0, 2_830L, 600f).startMs, 0.001)
        // panning to the left is free
        assertEquals(1_000.0, viewport.copy(startMs = 2_000.0).panned(-1_000.0, 2_830L, 600f).startMs, 0.001)
    }

    @Test
    fun `a viewport already in the future does not move to the right nor come back`() {
        // zoomed out or a short session: the start is past the limit
        val past = viewport.copy(startMs = 5_000.0)
        assertEquals(5_000.0, past.panned(300.0, 2_830L, 600f).startMs, 0.0)
        assertEquals(4_700.0, past.panned(-300.0, 2_830L, 600f).startMs, 0.0)
    }

    @Test
    fun `without a limit the viewport cannot be panned to the right`() {
        assertEquals(0.0, viewport.panned(500.0, null, 600f).startMs, 0.0)
        assertEquals(-200.0, viewport.panned(-200.0, null, 600f).startMs, 0.0)
    }

    @Test
    fun `the most recent session ends now, even with no new event`() {
        val started = model.copy(sessionStartTimestamp = 1_000_000L)

        // 10 s after the start, the last event is at 2830 ms: now is the limit
        assertEquals(10_000L, started.panLimitMs(isMostRecentSession = true, nowTimestamp = 1_010_000L))
        // a phone ahead of the computer: never before the last event
        assertEquals(2_830L, started.panLimitMs(isMostRecentSession = true, nowTimestamp = 1_001_000L))
        // an older session ends at its last event
        assertEquals(2_830L, started.panLimitMs(isMostRecentSession = false, nowTimestamp = 1_010_000L))
    }

    @Test
    fun `the latest given by the caller is used`() {
        val started = model.copy(sessionStartTimestamp = 1_000_000L)

        assertEquals(7_000L, started.panLimitMs(isMostRecentSession = false, nowTimestamp = 1_010_000L, latestMs = 7_000L))
    }

    @Test
    fun `a session without a known start ends at its last event`() {
        assertEquals(2_830L, model.panLimitMs(isMostRecentSession = true, nowTimestamp = 1_010_000L))
    }

    @Test
    fun `the most recent session without event ends now`() {
        val empty = buildGraphUiModel(emptyList(), 0L, sessionStartTimestamp = 1_000_000L)

        assertEquals(4_000L, empty.panLimitMs(isMostRecentSession = true, nowTimestamp = 1_004_000L))
        assertNull(empty.panLimitMs(isMostRecentSession = false, nowTimestamp = 1_004_000L))
    }

    @Test
    fun `the auto scroll on the present puts it at the right of the track`() {
        // 1 px per ms in a 600 px track: the right is the start + 520 ms (80 px of margin)
        val behind = viewport.copy(startMs = 0.0)

        assertEquals(10_000.0 - 520.0, behind.followedTo(10_000L, 600f).startMs, 0.001)
        // in view: nothing moves
        assertEquals(behind, behind.followedTo(400L, 600f))
        // never moves back
        assertEquals(viewport.copy(startMs = 5_000.0), viewport.copy(startMs = 5_000.0).followedTo(1_000L, 600f))
    }

    @Test
    fun `the distance of the present past the right of the track`() {
        assertEquals(480.0, viewport.copy(startMs = 0.0).pxPastRight(1_000L, 600f), 0.001)
        assertEquals(-120.0, viewport.copy(startMs = 0.0).pxPastRight(400L, 600f), 0.001)
        // 0.5 px per ms: the right of the track is at 1040 ms, 1520 ms is 480 ms further, 240 px
        assertEquals(240.0, GraphViewport(startMs = 0.0, pxPerMs = 0.5).pxPastRight(1_520L, 600f), 0.001)
    }

    @Test
    fun `reaching the end of the graph by moving to the right`() {
        // a limit at 2830 ms in a 600 px track: the end is at the start 2310 ms
        val before = viewport.copy(startMs = 2_000.0)

        assertTrue(before.reachedEnd(before.panned(5_000.0, 2_830L, 600f), 2_830L, 600f))
        // not far enough
        assertTrue(!before.reachedEnd(before.panned(100.0, 2_830L, 600f), 2_830L, 600f))
        // already there: a move that goes nowhere is not an arrival
        val there = viewport.copy(startMs = 2_310.0)
        assertTrue(!there.reachedEnd(there.panned(100.0, 2_830L, 600f), 2_830L, 600f))
        // past it (zoomed out, short session) and no limit
        val past = viewport.copy(startMs = 5_000.0)
        assertTrue(!past.reachedEnd(past.panned(100.0, 2_830L, 600f), 2_830L, 600f))
        assertTrue(!before.reachedEnd(before.panned(5_000.0, 2_830L, 600f), null, 600f))
    }

    @Test
    fun `the viewport never goes more than a second before the start of the session`() {
        assertEquals(-450.0, viewport.copy(startMs = 50.0).panned(-500.0, 2_830L, 600f).startMs, 0.0)
        assertEquals(MIN_START_MS, viewport.copy(startMs = 50.0).panned(-5_000.0, 2_830L, 600f).startMs, 0.0)
        assertEquals(60.0, viewport.copy(startMs = 50.0).panned(10.0, 2_830L, 600f).startMs, 0.0)
        // zooming out around the pointer would show the time before 0
        assertEquals(MIN_START_MS, viewport.copy(startMs = 10.0).zoomed(0.1, anchorX = 500f).startMs, 0.0)
    }

    @Test
    fun `zoom on a box`() {
        // 1 px per ms: the box from 100 to 300 fills a track of 600 px
        val zoomed = viewport.copy(startMs = 50.0).zoomedToRange(fromX = 300f, toX = 100f, trackWidthPx = 600f)

        assertEquals(150.0, zoomed.startMs, 0.001)
        assertEquals(3.0, zoomed.pxPerMs, 0.001)
        // too narrow: nothing changes
        assertEquals(viewport, viewport.zoomedToRange(100f, 104f, 600f))
        // out of the track: the box is cut, and never starts before 0
        assertEquals(0.0, viewport.zoomedToRange(-200f, 300f, 600f).startMs, 0.0)
    }

    @Test
    fun `the zoom in percent, 100 is 30 s in the track`() {
        // 600 px for 30 s
        assertEquals(100, GraphViewport(pxPerMs = 600.0 / 30_000).zoomPercent(600f))
        assertEquals(200, GraphViewport(pxPerMs = 600.0 / 15_000).zoomPercent(600f))
        assertEquals(50, GraphViewport(pxPerMs = 600.0 / 60_000).zoomPercent(600f))
        // a session of 3 s fitted in the track
        assertEquals(1_000, fitViewport(3_000L, 600f).zoomPercent(600f))
        // the graph opens at 100 %, from the start
        assertEquals(100, defaultViewport(600f).zoomPercent(600f))
        assertEquals(MIN_START_MS, defaultViewport(600f).startMs, 0.0)
    }

    @Test
    fun `zoom to a typed percent keeps the middle of the track`() {
        val start = GraphViewport(startMs = 10_000.0, pxPerMs = 600.0 / 60_000)
        val zoomed = start.zoomedToPercent(400, 600f)

        assertEquals(400, zoomed.zoomPercent(600f))
        assertEquals(start.msAt(300f), zoomed.msAt(300f), 0.001)
        // nothing to do for a percent that is not valid
        assertEquals(start, start.zoomedToPercent(0, 600f))
    }

    @Test
    fun `the label of an instant stops before the next event`() {
        val events = previewGraphEvents().filter { it.lane == GraphLane.Analytics }
        val first = events.first { it.row.id == "2" } // 20 ms
        val next = events.first { it.row.id == "9" } // 860 ms

        // 0.1 px per ms: 84 px between the two, the offset, the diamond and the gap are taken
        val room = instantLabelRoomPx(first, next, pxPerMs = 0.1, onTheLeft = false)
        assertEquals(84f - InstantLabelOffsetPx - InstantLabelGapPx - DiamondSizePx / 2f, room!!, 0.01f)
        // nothing after it: all the room
        assertNull(instantLabelRoomPx(first, null, 0.1, onTheLeft = false))
        // zoomed out far enough, there is no room left
        assertTrue(!showBarText(instantLabelRoomPx(first, next, pxPerMs = 0.02, onTheLeft = false)!!))
    }

    @Test
    fun `hit test finds an instant by its label`() {
        val analyticsTop = model.laneTops().first()
        // app_open at 20 ms, 1 px per ms: its label is on the right of the diamond, before screen_view at 860 ms
        val onLabel = model.hitTest(viewport, x = 20f + 14f + 20f, yInContent = analyticsTop + 10f)
        assertEquals("2", onLabel?.row?.id)
    }

    @Test
    fun `auto scroll follows the latest event only when it is out of view`() {
        // the latest event ends at 2830 ms. 1 px per ms, a track of 600 px: 520 ms of room with the margin
        val inView = GraphViewport(startMs = 2_500.0, pxPerMs = 1.0)
        assertEquals(inView, inView.followedTo(model.latestMs()!!, trackWidthPx = 600f))

        val behind = GraphViewport(startMs = 0.0, pxPerMs = 1.0)
        val followed = behind.followedTo(model.latestMs()!!, trackWidthPx = 600f)
        assertEquals(2830.0 - 520.0, followed.startMs, 0.001)
        // the zoom and the vertical scroll are kept
        assertEquals(behind.pxPerMs, followed.pxPerMs, 0.0)
        assertEquals(behind.scrollY, followed.scrollY, 0f)
        // no event, no screenshot: nothing to follow
        assertNull(GraphUiModel.Empty.latestMs())
    }

    @Test
    fun `auto scroll and jump go to a screenshot more recent than the events`() {
        val withShot = model.copy(screenshots = listOf(GraphScreenshotUiModel(timeMs = 5_000L, filePath = "a.jpg")))
        val behind = GraphViewport(startMs = 0.0, pxPerMs = 1.0)

        assertEquals(5_000L, withShot.latestMs())
        assertEquals(5_000.0 - 520.0, behind.followedTo(withShot.latestMs()!!, trackWidthPx = 600f).startMs, 0.001)
        val jumped = behind.jumpedToLatest(withShot, trackWidthPx = 600f, trackHeightPx = 100f)
        assertEquals(5_000.0 - 520.0, jumped.startMs, 0.001)
        // the row of the latest event is still the one shown (the track is 100 px high)
        val rowTop = withShot.rowTopOf(withShot.latestEvent()!!)!!
        assertTrue(rowTop >= jumped.scrollY && rowTop + RowHeightPx <= jumped.scrollY + 100f)
    }

    @Test
    fun `an older screenshot does not change the latest`() {
        val withShot = model.copy(screenshots = listOf(GraphScreenshotUiModel(timeMs = 100L, filePath = "a.jpg")))

        assertEquals(2_830L, withShot.latestMs())
    }

    @Test
    fun `a graph with only screenshots follows and jumps to the last one`() {
        val onlyShots = GraphUiModel.Empty.copy(screenshots = listOf(GraphScreenshotUiModel(timeMs = 4_000L, filePath = "a.jpg")))
        val behind = GraphViewport(startMs = 0.0, pxPerMs = 1.0)

        assertEquals(4_000.0 - 520.0, behind.followedTo(onlyShots.latestMs()!!, trackWidthPx = 600f).startMs, 0.001)
        assertEquals(4_000.0 - 520.0, behind.jumpedToLatest(onlyShots, trackWidthPx = 600f, trackHeightPx = 100f).startMs, 0.001)
    }
}
