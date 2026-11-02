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
        // the network lane starts at 0 inside the track, the app lane (one row) is above it
        val networkTop = RowHeightPx + LaneSeparatorPx
        val feed = model.hitTest(viewport, x = 400f, yInContent = networkTop + RowHeightPx + 10f)

        assertEquals("3", feed?.row?.id)
    }

    @Test
    fun `hit test finds a diamond with a tolerance`() {
        val analyticsTop = model.laneTops().last()
        val appOpen = model.hitTest(viewport, x = 20f + 9f, yInContent = analyticsTop + 10f) // 6 px of diamond and 4 of margin

        assertEquals("2", appOpen?.row?.id)
    }

    @Test
    fun `hit test finds nothing in the empty parts`() {
        val analyticsTop = model.laneTops().last()

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

        // the analytics event at 2830 ms ends last: it is at the right of the track, with a margin
        assertEquals("15", model.latestEvent()?.row?.id)
        assertEquals(2830.0 - 520.0, jumped.startMs, 0.001)
        assertTrue(jumped.scrollY > 0f)
    }

    @Test
    fun `the viewport never goes before the start of the session`() {
        assertEquals(0.0, viewport.copy(startMs = 50.0).panned(-500.0).startMs, 0.0)
        assertEquals(60.0, viewport.copy(startMs = 50.0).panned(10.0).startMs, 0.0)
        // zooming out around the pointer would show the time before 0
        assertEquals(0.0, viewport.copy(startMs = 10.0).zoomed(0.1, anchorX = 500f).startMs, 0.0)
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
    fun `the zoom in percent, 100 is 60 s in the track`() {
        // 600 px for 60 s
        assertEquals(100, GraphViewport(pxPerMs = 600.0 / 60_000).zoomPercent(600f))
        assertEquals(200, GraphViewport(pxPerMs = 600.0 / 30_000).zoomPercent(600f))
        assertEquals(50, GraphViewport(pxPerMs = 600.0 / 120_000).zoomPercent(600f))
        // a session of 3 s fitted in the track
        assertEquals(2_000, fitViewport(3_000L, 600f).zoomPercent(600f))
    }
}
