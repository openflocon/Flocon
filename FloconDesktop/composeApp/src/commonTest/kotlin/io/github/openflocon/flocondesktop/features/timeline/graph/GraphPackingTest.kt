package io.github.openflocon.flocondesktop.features.timeline.graph

import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GraphPackingTest {

    private fun ids(model: GraphUiModel, lane: GraphLane) = model.lanes.first { it.lane == lane }.rows.map { row -> row.map { it.row.id } }

    @Test
    fun `the reference session gives 7 rows`() {
        val model = buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs)

        assertEquals(7, model.rowCount)
        assertEquals(listOf(listOf("0")), ids(model, GraphLane.App))
        assertEquals(
            listOf(listOf("1", "4", "10", "14"), listOf("3", "12"), listOf("6"), listOf("7")),
            ids(model, GraphLane.Network),
        )
        assertEquals(listOf(listOf("5", "11")), ids(model, GraphLane.Database))
        assertEquals(listOf(listOf("2", "9", "13", "15")), ids(model, GraphLane.Analytics))
    }

    @Test
    fun `the lanes keep their order`() {
        val model = buildGraphUiModel(previewGraphEvents().reversed(), PreviewSessionDurationMs)

        assertEquals(GraphLane.entries, model.lanes.map { it.lane })
    }

    @Test
    fun `the summary of the reference session`() {
        val model = buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs)

        assertEquals(15, model.eventCount)
        assertEquals(2, model.errorCount)
        assertEquals("/v1/recommendations", model.slowest?.name)
        val text = model.toSummaryText()
        assertEquals("3.00 s", text.sessionDuration)
        assertEquals("1.21 s", text.slowestDuration)
    }

    @Test
    fun `an empty lane is hidden`() {
        val model = buildGraphUiModel(previewGraphEvents().filter { it.lane == GraphLane.Network }, 1_000L)

        assertEquals(listOf(GraphLane.Network), model.lanes.map { it.lane })
        assertTrue(buildGraphUiModel(emptyList(), 0L).lanes.isEmpty())
    }

    @Test
    fun `the gap decides if an event fits after another`() {
        fun event(id: String, start: Long, duration: Long) = previewGraphEvents().first { it.row.id == "1" }.let {
            it.copy(row = it.row.copy(id = id), startMs = start, durationMs = duration)
        }

        // ends at 100, plus the gap of 15
        val fits = packLane(GraphLane.Network, listOf(event("a", 0, 100), event("b", 100 + GAP_MS, 10)))
        val overlaps = packLane(GraphLane.Network, listOf(event("a", 0, 100), event("b", 100 + GAP_MS - 1, 10)))

        assertEquals(1, fits.rows.size)
        assertEquals(2, overlaps.rows.size)
    }

    @Test
    fun `an instant reserves its room`() {
        val instants = previewGraphEvents().filter { it.lane == GraphLane.Analytics }.take(2)
            .mapIndexed { index, event -> event.copy(startMs = index * (INSTANT_OCCUPANCY_MS + GAP_MS - 1)) }

        // the second starts one ms before the end of the room of the first (320 ms and the gap)
        assertEquals(2, packLane(GraphLane.Analytics, instants).rows.size)
    }

    @Test
    fun `two events at the same start are ordered by key`() {
        val base = previewGraphEvents().first { it.row.id == "1" }
        val a = base.copy(row = base.row.copy(id = "b"), startMs = 5, durationMs = 10)
        val b = base.copy(row = base.row.copy(id = "a"), startMs = 5, durationMs = 10)

        val lane = packLane(GraphLane.Network, listOf(a, b))

        assertEquals(listOf("a"), lane.rows[0].map { it.row.id })
        assertEquals(listOf("b"), lane.rows[1].map { it.row.id })
    }

    @Test
    fun `50000 events are packed fast, with a burst of 500 calls`() {
        val base = previewGraphEvents().first { it.row.id == "1" }
        val events = List(50_000) { i ->
            base.copy(row = base.row.copy(id = "e$i"), startMs = (i / 500) * 300L, durationMs = 200L)
        }

        var model: GraphUiModel
        val elapsed = measureTimeMillis { model = buildGraphUiModel(events, 30_000L) }

        assertEquals(50_000, model.eventCount)
        // generous: the target is 100 ms on the dev machine, a CI machine can be slower
        assertTrue(elapsed < 5_000, "took $elapsed ms")
    }

    private fun domain(id: String, phase: TimelinePhase, ts: Long, duration: Double?, source: TimelineSource = TimelineSource.Network) = TimelineEventDomainModel(
        id = id,
        source = source,
        timestamp = ts,
        title = "https://api.shop.io/v1/feed?page=2",
        subtitle = null,
        appInstance = 1_000L,
        method = "GET",
        statusCode = 200,
        durationMs = duration,
        phase = phase,
    )

    @Test
    fun `a request and its response are one bar`() {
        val model = buildGraph(
            listOf(
                domain("1", TimelinePhase.Response, ts = 1_640L, duration = 640.0),
                domain("1", TimelinePhase.Request, ts = 1_000L, duration = 640.0),
            ),
            sessionStart = 1_000L,
        )

        val events = model.lanes.single().rows.flatten()
        assertEquals(1, events.size)
        assertEquals(0L, events.single().startMs)
        assertEquals(640L, events.single().durationMs)
        assertEquals("/v1/feed", events.single().name)
    }

    @Test
    fun `a call without response goes up to the last event`() {
        val model = buildGraph(
            listOf(
                domain("1", TimelinePhase.Request, ts = 1_100L, duration = null),
                domain("a", TimelinePhase.Request, ts = 1_900L, duration = 10.0),
            ),
            sessionStart = 1_000L,
        )

        val pending = model.lanes.single().rows.flatten().first { it.row.id == "1" }
        assertTrue(pending.inProgress)
        assertEquals(800L, pending.durationMs)
    }

    @Test
    fun `a response alone starts before itself`() {
        val model = buildGraph(listOf(domain("1", TimelinePhase.Response, ts = 1_500L, duration = 300.0)), sessionStart = 1_000L)

        assertEquals(200L, model.lanes.single().rows.single().single().startMs)
    }

    @Test
    fun `the sources without lane are not drawn`() {
        val model = buildGraph(listOf(domain("c", TimelinePhase.Request, ts = 1_100L, duration = null, source = TimelineSource.Crash)), 1_000L)

        assertTrue(model.lanes.isEmpty())
        assertNull(model.slowest)
    }
}
