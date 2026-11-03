package io.github.openflocon.mcp.timeline

import io.github.openflocon.mcp.McpToolException
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimelineWindowResolverTest {

    private val zone = ZoneId.of("Europe/Paris")

    // Tuesday 2026-11-03, 15:00 in Paris
    private val now = ZonedDateTime.of(2026, 11, 3, 15, 0, 0, 0, zone).toInstant().toEpochMilli()
    private val resolver = TimelineWindowResolver(zone = zone, nowMs = { now })

    private fun at(hour: Int, minute: Int, second: Int = 0, day: Int = 3) = ZonedDateTime.of(2026, 11, day, hour, minute, second, 0, zone).toInstant().toEpochMilli()

    private fun crash(id: String, ms: Long) = TimelineCrash(id = id, timestampMs = ms, title = "IllegalStateException")

    private fun resolve(input: GetTimelineInput = GetTimelineInput(), anchors: TimelineAnchors, event: TimelineEventAnchor? = null) = resolver.resolve(input, anchors, event)

    @Test
    fun `no input anchors on the crash of the latest session`() {
        val crashTime = at(14, 32, 5)
        val anchors = TimelineAnchors(
            sessionStarts = listOf(at(13, 0), at(14, 0)),
            crashes = listOf(crash("1", at(13, 30)), crash("2", crashTime)),
            lastEventMs = at(14, 40),
        )

        val window = resolve(anchors = anchors)

        assertEquals(crashTime - 60_000, window.fromMs)
        assertEquals(crashTime + 5_000, window.toMs)
        assertEquals("crash:2", window.anchorEventId)
        assertTrue(window.reason.contains("IllegalStateException"), window.reason)
        assertTrue(window.reason.contains("14:32:05"), window.reason)
    }

    @Test
    fun `a crash just before the latest session anchors the window when that session started within 60 s of it`() {
        val crashTime = at(14, 0, 0)
        val anchors = TimelineAnchors(
            sessionStarts = listOf(at(13, 0), at(14, 0, 40)),
            crashes = listOf(crash("9", crashTime)),
            lastEventMs = at(14, 5),
        )

        val window = resolve(anchors = anchors)

        assertEquals("crash:9", window.anchorEventId)
        assertEquals(crashTime - 60_000, window.fromMs)
    }

    @Test
    fun `an older crash before the latest session is ignored`() {
        val anchors = TimelineAnchors(
            sessionStarts = listOf(at(13, 0), at(14, 0)),
            crashes = listOf(crash("1", at(13, 30))),
            lastEventMs = at(14, 10),
        )

        val window = resolve(anchors = anchors)

        assertNull(window.anchorEventId)
    }

    @Test
    fun `no crash gives the last 2 minutes of the latest session`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(13, 0), at(14, 0)), crashes = emptyList(), lastEventMs = at(14, 10))

        val window = resolve(anchors = anchors)

        assertEquals(at(14, 8), window.fromMs)
        assertEquals(at(14, 10), window.toMs)
        assertTrue(window.reason.contains("2 minutes"), window.reason)
    }

    @Test
    fun `the default window does not start before the latest session`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(14, 0)), crashes = emptyList(), lastEventMs = at(14, 1))

        val window = resolve(anchors = anchors)

        assertEquals(at(14, 0), window.fromMs)
    }

    @Test
    fun `recent is measured from the last event, not from the clock`() {
        val lastEvent = at(9, 0)
        val anchors = TimelineAnchors(sessionStarts = listOf(at(8, 0)), crashes = emptyList(), lastEventMs = lastEvent)

        val window = resolve(GetTimelineInput(mode = TimelineMode.Recent, minutes = 10), anchors)

        assertEquals(lastEvent - 600_000, window.fromMs)
        assertEquals(lastEvent, window.toMs)
    }

    @Test
    fun `the mode session covers the latest session until the last event`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(8, 0), at(12, 0)), crashes = emptyList(), lastEventMs = at(12, 30))

        val window = resolve(GetTimelineInput(mode = TimelineMode.Session), anchors)

        assertEquals(at(12, 0), window.fromMs)
        assertEquals(at(12, 30), window.toMs)
    }

    @Test
    fun `the mode before_crash ends just after the latest crash`() {
        val crashTime = at(10, 0)
        val anchors = TimelineAnchors(sessionStarts = listOf(at(9, 0)), crashes = listOf(crash("4", crashTime)), lastEventMs = at(10, 30))

        val window = resolve(GetTimelineInput(mode = TimelineMode.BeforeCrash), anchors)

        assertEquals(crashTime - 60_000, window.fromMs)
        assertEquals(crashTime + 5_000, window.toMs)
    }

    @Test
    fun `the mode before_crash without crash explains it`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(9, 0)), crashes = emptyList(), lastEventMs = at(10, 30))

        val error = assertFailsWith<McpToolException> { resolve(GetTimelineInput(mode = TimelineMode.BeforeCrash), anchors) }

        assertTrue(error.message!!.contains("crash"), error.message)
    }

    @Test
    fun `HH_mm resolves to its latest past occurrence`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(8, 0)), crashes = emptyList(), lastEventMs = at(14, 50))

        val earlierToday = resolve(GetTimelineInput(mode = TimelineMode.At, at = "14:32", aroundSeconds = 30), anchors)
        val laterThanNow = resolve(GetTimelineInput(mode = TimelineMode.At, at = "16:10:15", aroundSeconds = 30), anchors)

        assertEquals(at(14, 32) - 30_000, earlierToday.fromMs)
        assertEquals(at(14, 32) + 30_000, earlierToday.toMs)
        // 16:10 has not happened yet today: it was yesterday
        assertEquals(at(16, 10, 15, day = 2) - 30_000, laterThanNow.fromMs)
    }

    @Test
    fun `an ISO instant with an offset and epoch milliseconds are read`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(8, 0)), crashes = emptyList(), lastEventMs = at(14, 50))

        val iso = resolve(GetTimelineInput(mode = TimelineMode.At, at = "2026-11-03T14:32:00+01:00", aroundSeconds = 10), anchors)
        val epoch = resolve(GetTimelineInput(mode = TimelineMode.At, at = at(14, 32).toString(), aroundSeconds = 10), anchors)

        assertEquals(at(14, 32) - 10_000, iso.fromMs)
        assertEquals(iso.fromMs, epoch.fromMs)
    }

    @Test
    fun `an unreadable time lists the accepted formats`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(8, 0)), crashes = emptyList(), lastEventMs = at(14, 50))

        val error = assertFailsWith<McpToolException> { resolve(GetTimelineInput(mode = TimelineMode.At, at = "tomorrow-ish"), anchors) }

        assertTrue(error.message!!.contains("HH:mm"), error.message)
    }

    @Test
    fun `the mode at without time explains it`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(8, 0)), crashes = emptyList(), lastEventMs = at(14, 50))

        assertFailsWith<McpToolException> { resolve(GetTimelineInput(mode = TimelineMode.At), anchors) }
    }

    @Test
    fun `the mode range goes from to to, to defaults to the last event`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(8, 0)), crashes = emptyList(), lastEventMs = at(14, 50))

        val closed = resolve(GetTimelineInput(mode = TimelineMode.Range, from = "10:00", to = "10:05"), anchors)
        val open = resolve(GetTimelineInput(mode = TimelineMode.Range, from = "10:00"), anchors)

        assertEquals(at(10, 0), closed.fromMs)
        assertEquals(at(10, 5), closed.toMs)
        assertEquals(at(14, 50), open.toMs)
    }

    @Test
    fun `an instant before the first known session keeps the requested window and says so`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(12, 0)), crashes = emptyList(), lastEventMs = at(14, 50))

        val window = resolve(GetTimelineInput(mode = TimelineMode.At, at = "09:00"), anchors)

        assertEquals(at(9, 0) - 30_000, window.fromMs)
        assertTrue(window.reason.contains("before the first known session"), window.reason)
    }

    @Test
    fun `an event anchor gives a window around the event`() {
        val anchors = TimelineAnchors(sessionStarts = listOf(at(8, 0)), crashes = emptyList(), lastEventMs = at(14, 50))
        val event = TimelineEventAnchor(id = "network:abc", timestampMs = at(11, 0), label = "GET /feed")

        val window = resolve(GetTimelineInput(eventId = "network:abc", aroundSeconds = 20), anchors, event)

        assertEquals(at(11, 0) - 20_000, window.fromMs)
        assertEquals(at(11, 0) + 20_000, window.toMs)
        assertEquals("network:abc", window.anchorEventId)
        assertTrue(window.reason.contains("GET /feed"), window.reason)
    }

    @Test
    fun `no events gives a window with a reason, not an exception`() {
        val window = resolve(anchors = TimelineAnchors(sessionStarts = emptyList(), crashes = emptyList(), lastEventMs = null))

        assertEquals(0L, window.fromMs)
        assertEquals(0L, window.toMs)
        assertTrue(window.reason.contains("No event"), window.reason)
    }
}
