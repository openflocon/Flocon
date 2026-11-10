package io.github.openflocon.flocondesktop.features.timeline.graph

import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.timeline.mapper.LogGlyph
import io.github.openflocon.flocondesktop.features.timeline.mapper.LogGlyphColor
import io.github.openflocon.flocondesktop.features.timeline.mapper.LogGlyphShape
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GraphPackingTest {

    private fun ids(model: GraphUiModel, lane: GraphLane) = model.lanes.first { it.lane == lane }.rows.map { row -> row.map { it.row.id } }

    @Test
    fun `the reference session gives 7 rows`() {
        val model = buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs)

        assertEquals(7, model.rowCount)
        assertEquals(
            listOf(listOf("1", "4", "10", "14"), listOf("3", "12"), listOf("6"), listOf("7")),
            ids(model, GraphLane.Network),
        )
        assertEquals(listOf(listOf("5", "11")), ids(model, GraphLane.Database))
        assertEquals(listOf(listOf("2", "9", "13", "15")), ids(model, GraphLane.Analytics))
        assertEquals(listOf(listOf("16", "17", "18")), ids(model, GraphLane.Log))
    }

    @Test
    fun `logLaneIsPresent`() {
        val model = buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs)

        assertTrue(GraphLane.Log in model.lanes.map { it.lane })
        assertEquals(TimelineSource.Log, GraphLane.Log.source)
        assertEquals(GraphLane.Log, TimelineSource.Log.toGraphLane())
    }

    @Test
    fun `logEventBecomesInstantMarker`() {
        val model = buildGraph(listOf(domain("l1", TimelinePhase.Request, ts = 1_250L, duration = null, source = TimelineSource.Log).copy(title = "Opened the feed", method = null, statusCode = null)), sessionStart = 1_000L)

        val marker = model.lanes.single().rows.single().single()
        assertEquals(GraphLane.Log, marker.lane)
        assertTrue(marker.isInstant)
        assertEquals(250L, marker.startMs)
        assertEquals("Opened the feed", marker.name)
        assertFalse(marker.isError)
    }

    @Test
    fun `logMarkersCarryTheirLevelAndOnlyErrorFails`() {
        val model = buildGraph(
            TimelineLogLevel.entries.map { level ->
                domain(level.name, TimelinePhase.Request, ts = 1_250L, duration = null, source = TimelineSource.Log)
                    .copy(method = null, statusCode = null, hasError = level == TimelineLogLevel.Error, level = level)
            },
            sessionStart = 1_000L,
        )

        val markers = model.lanes.single().rows.flatten().associateBy { it.row.id }
        TimelineLogLevel.entries.forEach { level ->
            assertEquals(level, markers.getValue(level.name).logLevel)
            assertEquals(level == TimelineLogLevel.Error, markers.getValue(level.name).isError)
        }
        assertEquals(1, model.errorCount)
    }

    @Test
    fun `logMarkersCarryTheirGlyphAndTypedLogsAreNeverErrors`() {
        fun log(id: String, type: String?, level: TimelineLogLevel) = domain(id, TimelinePhase.Request, ts = 1_250L, duration = null, source = TimelineSource.Log)
            .copy(subtitle = type, method = null, statusCode = null, hasError = level == TimelineLogLevel.Error, level = level)
        val model = buildGraph(
            listOf(
                log("nav", "navigation", TimelineLogLevel.Info),
                log("act", "userAction", TimelineLogLevel.Info),
                log("life", "Lifecycle", TimelineLogLevel.Info),
                log("tag", "billing", TimelineLogLevel.Warning),
                log("err", "billing", TimelineLogLevel.Error),
            ),
            sessionStart = 1_000L,
        )

        val markers = model.lanes.single().rows.flatten().associateBy { it.row.id }
        assertEquals(LogGlyph(LogGlyphShape.Arrow, LogGlyphColor.Navigation), markers.getValue("nav").logGlyph)
        assertEquals(LogGlyph(LogGlyphShape.Pointer, LogGlyphColor.UserAction), markers.getValue("act").logGlyph)
        assertEquals(LogGlyph(LogGlyphShape.Heart, LogGlyphColor.Lifecycle), markers.getValue("life").logGlyph)
        assertEquals(LogGlyph(LogGlyphShape.Diamond, LogGlyphColor.Warning), markers.getValue("tag").logGlyph)
        assertEquals(LogGlyph(LogGlyphShape.Diamond, LogGlyphColor.Error), markers.getValue("err").logGlyph)
        assertEquals(listOf("err"), markers.values.filter { it.isError }.map { it.row.id })
        assertEquals(1, model.errorCount)
    }

    @Test
    fun `networkEventHasNoLogGlyph`() {
        val model = buildGraph(listOf(domain("1", TimelinePhase.Request, ts = 1_000L, duration = 100.0)), sessionStart = 1_000L)

        assertNull(model.lanes.single().rows.single().single().logGlyph)
        assertNull(model.lanes.single().rows.single().single().logType)
    }

    @Test
    fun `networkEventHasNoLogLevel`() {
        val model = buildGraph(listOf(domain("1", TimelinePhase.Request, ts = 1_000L, duration = 100.0)), sessionStart = 1_000L)

        assertEquals(null, model.lanes.single().rows.single().single().logLevel)
    }

    @Test
    fun `errorLevelLogMarkedAsError`() {
        // hasError is computed in SQL (level = ERROR): the graph only carries it
        val model = buildGraph(
            listOf(domain("l1", TimelinePhase.Request, ts = 1_250L, duration = null, source = TimelineSource.Log).copy(method = null, statusCode = null, hasError = true, level = TimelineLogLevel.Error)),
            sessionStart = 1_000L,
        )

        assertTrue(model.lanes.single().rows.single().single().isError)
        assertEquals(1, model.errorCount)
    }

    @Test
    fun `the lanes keep their order`() {
        val model = buildGraphUiModel(previewGraphEvents().reversed(), PreviewSessionDurationMs)

        assertEquals(GraphLane.entries, model.lanes.map { it.lane })
    }

    @Test
    fun `the summary of the reference session`() {
        val model = buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs)

        assertEquals(17, model.eventCount)
        assertEquals(3, model.errorCount)
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
    fun `a call without response is not drawn`() {
        val model = buildGraph(
            listOf(
                domain("1", TimelinePhase.Request, ts = 1_100L, duration = null),
                domain("a", TimelinePhase.Request, ts = 1_900L, duration = 10.0),
            ),
            sessionStart = 1_000L,
        )

        assertEquals(listOf("a"), model.lanes.single().rows.flatten().map { it.row.id })
    }

    @Test
    fun `a response alone starts before itself`() {
        val model = buildGraph(listOf(domain("1", TimelinePhase.Response, ts = 1_500L, duration = 300.0)), sessionStart = 1_000L)

        assertEquals(200L, model.lanes.single().rows.single().single().startMs)
    }

    @Test
    fun `a failed call without http code is an error`() {
        val failed = buildGraph(
            listOf(domain("1", TimelinePhase.Request, ts = 1_100L, duration = 20.0).copy(statusCode = null, hasError = true)),
            sessionStart = 1_000L,
        )
        val ok = buildGraph(listOf(domain("1", TimelinePhase.Request, ts = 1_100L, duration = 20.0)), sessionStart = 1_000L)

        assertEquals(1, failed.errorCount)
        assertEquals(0, ok.errorCount)
    }

    @Test
    fun `the sources without lane are not drawn`() {
        val model = buildGraph(listOf(domain("c", TimelinePhase.Request, ts = 1_100L, duration = null, source = TimelineSource.Crash)), 1_000L)

        assertTrue(model.lanes.isEmpty())
        assertNull(model.slowest)
    }

    @Test
    fun `a websocket message is an instant, not a call waiting for its response`() {
        val model = buildGraph(
            listOf(
                domain("ws1", TimelinePhase.Request, ts = 1_100L, duration = null).copy(method = "WS", statusCode = null),
                domain("a", TimelinePhase.Request, ts = 1_900L, duration = 10.0),
            ),
            sessionStart = 1_000L,
        )

        val message = model.lanes.single().rows.flatten().first { it.row.id == "ws1" }
        assertTrue(message.isInstant)
        assertEquals(100L, message.startMs)
    }

    @Test
    fun `the app start is a line, not a lane`() {
        val withStart = buildGraph(listOf(domain("s", TimelinePhase.Request, ts = 1_000L, duration = null, source = TimelineSource.AppStart)), sessionStart = 1_000L)
        val without = buildGraph(listOf(domain("1", TimelinePhase.Request, ts = 1_100L, duration = 20.0)), sessionStart = 1_000L)

        assertTrue(withStart.hasAppStart)
        assertTrue(withStart.lanes.isEmpty())
        assertFalse(without.hasAppStart)
    }
}
