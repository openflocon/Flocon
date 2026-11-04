package io.github.openflocon.mcp.timeline

import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimelineNarratorTest {

    private val zone = ZoneId.of("UTC")
    private val narrator = TimelineNarrator(zone)

    // 2026-11-03 14:32:00 UTC
    private val base = ZonedDateTime.of(2026, 11, 3, 14, 32, 0, 0, zone).toInstant().toEpochMilli()

    private fun net(
        id: String,
        offsetMs: Long,
        path: String = "/v1/feed",
        method: String = "GET",
        status: Int? = 200,
        bytes: Long? = null,
        hasError: Boolean = false,
        image: Boolean = false,
        phase: TimelinePhase = TimelinePhase.Request,
    ) = TimelineEventDomainModel(
        id = id,
        source = TimelineSource.Network,
        timestamp = base + offsetMs,
        title = "https://api.shop.io$path",
        subtitle = null,
        appInstance = null,
        method = method,
        statusCode = status,
        durationMs = 120.0,
        phase = phase,
        isImage = image,
        hasError = hasError,
        responseByteSize = bytes,
    )

    private fun event(id: String, offsetMs: Long, source: TimelineSource, title: String, subtitle: String? = null) = TimelineEventDomainModel(
        id = id,
        source = source,
        timestamp = base + offsetMs,
        title = title,
        subtitle = subtitle,
        appInstance = null,
    )

    private fun log(id: String, offsetMs: Long, type: String, value: String, level: TimelineLogLevel? = null) = TimelineEventDomainModel(
        id = id,
        source = TimelineSource.Log,
        timestamp = base + offsetMs,
        title = value,
        subtitle = type,
        appInstance = null,
        level = level,
    )

    // newest first, like the repository
    private fun narrate(
        events: List<TimelineEventDomainModel>,
        anchorMs: Long = base,
        search: TimelineSearch? = null,
        maxLines: Int = 150,
    ) = narrator.narrate(events.sortedByDescending { it.timestamp }, anchorMs, search, maxLines)

    @Test
    fun `a line tells the gap, the time, the call, its status, duration, size and id`() {
        val narrative = narrate(
            listOf(
                net("a", 0, status = 200, bytes = 3_500_000),
                event("e", 320, TimelineSource.Analytics, "app_open", "Firebase"),
            ),
        )

        assertEquals(2, narrative.lines.size)
        assertTrue(narrative.lines[0].contains("14:32:00.000"), narrative.lines[0])
        assertTrue(narrative.lines[0].contains("GET /v1/feed"), narrative.lines[0])
        assertTrue(narrative.lines[0].contains("200"), narrative.lines[0])
        assertTrue(narrative.lines[0].contains("3.3 MB"), narrative.lines[0])
        assertTrue(narrative.lines[0].endsWith("[network:a]"), narrative.lines[0])
        assertTrue(narrative.lines[1].startsWith("+320 ms"), narrative.lines[1])
        assertTrue(narrative.lines[1].contains("app_open"), narrative.lines[1])
        assertTrue(narrative.lines[1].endsWith("[analytics:e]"), narrative.lines[1])
    }

    @Test
    fun `the response events of the calls are not lines of their own`() {
        val narrative = narrate(listOf(net("a", 0), net("a", 120, phase = TimelinePhase.Response)))

        assertEquals(1, narrative.lines.size)
        assertEquals(1, narrative.eventCount)
    }

    @Test
    fun `three consecutive identical calls within 2 s fold into one line`() {
        val narrative = narrate(listOf(net("a", 0), net("b", 500), net("c", 1_000)))

        assertEquals(1, narrative.lines.size)
        assertTrue(narrative.lines[0].contains("3× GET /v1/feed"), narrative.lines[0])
        assertTrue(narrative.lines[0].contains("[network:a … network:c]"), narrative.lines[0])
    }

    @Test
    fun `two identical calls are not folded`() {
        val narrative = narrate(listOf(net("a", 0), net("b", 500)))

        assertEquals(2, narrative.lines.size)
    }

    @Test
    fun `calls more than 2 s apart are not folded`() {
        val narrative = narrate(listOf(net("a", 0), net("b", 2_500), net("c", 5_000)))

        assertEquals(3, narrative.lines.size)
    }

    @Test
    fun `an error is never folded with successes`() {
        val narrative = narrate(
            listOf(
                net("a", 0),
                net("b", 200),
                net("c", 400),
                net("d", 600, status = 500),
                net("e", 800),
                net("f", 1_000),
                net("g", 1_200),
            ),
        )

        assertEquals(3, narrative.lines.size)
        assertTrue(narrative.lines[0].contains("3×"), narrative.lines[0])
        assertTrue(narrative.lines[1].contains("500"), narrative.lines[1])
        assertFalse(narrative.lines[1].contains("×"), narrative.lines[1])
        assertTrue(narrative.lines[2].contains("3×"), narrative.lines[2])
    }

    @Test
    fun `the numeric and uuid segments of a path do not tell calls apart`() {
        val narrative = narrate(
            listOf(
                net("a", 0, path = "/v1/users/101/posts"),
                net("b", 300, path = "/v1/users/202/posts"),
                net("c", 600, path = "/v1/users/3f2c8a4e-1b2d-4c3e-9f10-aabbccddeeff/posts"),
            ),
        )

        assertEquals(1, narrative.lines.size)
        assertTrue(narrative.lines[0].contains("3×"), narrative.lines[0])
    }

    @Test
    fun `5 identical calls in 10 s are flagged as a loop`() {
        val narrative = narrate((0 until 5).map { net("c$it", it * 2_000L) })

        assertTrue(narrative.anomalies.any { it.startsWith("LOOP") && it.contains("5×") }, narrative.anomalies.toString())
    }

    @Test
    fun `4 identical calls in 10 s are not a loop`() {
        val narrative = narrate((0 until 4).map { net("c$it", it * 2_000L) })

        assertTrue(narrative.anomalies.none { it.startsWith("LOOP") }, narrative.anomalies.toString())
    }

    @Test
    fun `an http error and a failed call are flagged`() {
        val narrative = narrate(
            listOf(net("a", 0, status = 503), net("b", 10_000, status = null, hasError = true)),
        )

        assertEquals(2, narrative.anomalies.count { it.startsWith("HTTP") || it.startsWith("FAILED") }, narrative.anomalies.toString())
    }

    @Test
    fun `a response over 1 MB is flagged as large, below it is not`() {
        val narrative = narrate(listOf(net("a", 0, bytes = 2_000_000), net("b", 10_000, path = "/small", bytes = 1_048_575)))

        assertEquals(1, narrative.anomalies.count { it.startsWith("LARGE") }, narrative.anomalies.toString())
        assertTrue(narrative.anomalies.first { it.startsWith("LARGE") }.contains("1.9 MB"))
    }

    @Test
    fun `an image over 1 MB is labelled as an image`() {
        val narrative = narrate(listOf(net("a", 0, path = "/img/hero.jpg", bytes = 5_000_000, image = true)))

        assertTrue(narrative.anomalies.single().startsWith("LARGE image"), narrative.anomalies.toString())
    }

    @Test
    fun `a crash is an anomaly and a line, never folded`() {
        val narrative = narrate(
            listOf(
                event("1", 0, TimelineSource.Crash, "IllegalStateException", "bad state"),
                event("2", 500, TimelineSource.Crash, "IllegalStateException", "bad state"),
                event("3", 1_000, TimelineSource.Crash, "IllegalStateException", "bad state"),
            ),
        )

        assertEquals(3, narrative.lines.size)
        assertEquals(3, narrative.anomalies.count { it.startsWith("CRASH") })
    }

    @Test
    fun `a navigation log tells its type and value, a tagged error log is an anomaly`() {
        val narrative = narrate(
            listOf(
                log("1", 0, "navigation", "HomeScreen"),
                log("2", 500, "checkout", "payment declined", TimelineLogLevel.Error),
                log("3", 900, "checkout", "retrying", TimelineLogLevel.Warning),
            ),
        )

        assertTrue(narrative.lines[0].contains("LOG  navigation: HomeScreen  [log:1]"), narrative.lines[0])
        assertTrue(narrative.lines[1].contains("LOG  checkout [ERROR]: payment declined  [log:2]"), narrative.lines[1])
        assertTrue(narrative.lines[2].contains("checkout [WARNING]: retrying"), narrative.lines[2])
        assertEquals(1, narrative.anomalyCount)
        assertTrue(narrative.anomalies.single().startsWith("ERROR LOG checkout: payment declined"), narrative.anomalies.toString())
        assertTrue(narrative.anomalies.single().endsWith("[log:2]"), narrative.anomalies.toString())
    }

    @Test
    fun `identical logs fold, an error log is not folded with an info log`() {
        val narrative = narrate(
            listOf(
                log("1", 0, "userAction", "tap_buy"),
                log("2", 400, "userAction", "tap_buy"),
                log("3", 800, "userAction", "tap_buy"),
                log("4", 1_000, "userAction", "tap_buy", TimelineLogLevel.Error),
            ),
        )

        assertEquals(2, narrative.lines.size)
        assertTrue(narrative.lines[0].contains("3× userAction: tap_buy"), narrative.lines[0])
        assertTrue(narrative.lines[0].contains("[log:1 … log:3]"), narrative.lines[0])
    }

    @Test
    fun `search matches the value and the type of a log and can be limited to the log source`() {
        val events = listOf(
            log("1", 0, "navigation", "CheckoutScreen"),
            log("2", 500, "lifecycle", "background"),
            event("a", 900, TimelineSource.Analytics, "checkout_failed"),
        )

        val byValue = narrate(events, search = TimelineSearch("checkout", TimelineSource.Log))
        val byType = narrate(events, search = TimelineSearch("lifecycle", null))
        val wrongSource = narrate(events, search = TimelineSearch("checkout_failed", TimelineSource.Log))

        assertEquals(1, byValue.searchMatches.size)
        assertTrue(byValue.searchMatches.single().endsWith("[log:1]"))
        assertEquals(listOf(true, false), listOf(byType.found, wrongSource.found))
    }

    @Test
    fun `the anomalies are the first thing listed and are limited to 20`() {
        val narrative = narrate((0 until 30).map { event("$it", it * 1_000L, TimelineSource.Crash, "Boom$it") })

        assertEquals(20, narrative.anomalies.size)
        assertEquals(30, narrative.anomalyCount)
    }

    @Test
    fun `search matches survive the cap and are listed with found = true`() {
        val events = (0 until 10).map { net("n$it", it * 5_000L, path = "/v1/other$it") } +
            event("needle", 7_000, TimelineSource.Analytics, "checkout_failed", "Firebase")

        val narrative = narrate(events, anchorMs = base, maxLines = 2, search = TimelineSearch(text = "checkout_failed", source = null))

        assertEquals(true, narrative.found)
        assertEquals(1, narrative.searchMatches.size)
        assertTrue(narrative.searchMatches.single().contains("checkout_failed"), narrative.searchMatches.toString())
        assertTrue(narrative.truncated)
    }

    @Test
    fun `a search with no match gives found = false and no search gives null`() {
        val events = listOf(net("a", 0))

        assertEquals(false, narrate(events, search = TimelineSearch(text = "nope", source = null)).found)
        assertNull(narrate(events).found)
    }

    @Test
    fun `the search can be limited to a source`() {
        val events = listOf(
            net("a", 0, path = "/checkout"),
            event("b", 100, TimelineSource.Analytics, "checkout_started"),
        )

        val narrative = narrate(events, search = TimelineSearch(text = "checkout", source = TimelineSource.Analytics))

        assertEquals(1, narrative.searchMatches.size)
        assertTrue(narrative.searchMatches.single().contains("[analytics:b]"))
    }

    @Test
    fun `the cap reports truncated and omittedCount`() {
        val narrative = narrate((0 until 10).map { net("n$it", it * 5_000L, path = "/v1/other$it") }, maxLines = 3)

        assertEquals(3, narrative.lines.size)
        assertTrue(narrative.truncated)
        assertEquals(7, narrative.omittedCount)
    }

    @Test
    fun `when truncating, the lines nearest to the anchor are kept, in time order`() {
        val events = (0 until 10).map { net("n$it", it * 5_000L, path = "/v1/other$it") }

        val narrative = narrate(events, anchorMs = base + 25_000, maxLines = 3)

        assertEquals(listOf("[network:n4]", "[network:n5]", "[network:n6]"), narrative.lines.map { it.substringAfterLast("  ") })
    }

    @Test
    fun `no truncation is reported when everything fits`() {
        val narrative = narrate(listOf(net("a", 0)))

        assertFalse(narrative.truncated)
        assertEquals(0, narrative.omittedCount)
    }

    @Test
    fun `no event gives an empty narrative`() {
        val narrative = narrate(emptyList())

        assertTrue(narrative.lines.isEmpty())
        assertTrue(narrative.anomalies.isEmpty())
        assertEquals(0, narrative.eventCount)
    }

    @Test
    fun `a continuous poll is one loop, not one per burst`() {
        val narrative = narrate((0 until 60).map { net("p$it", it * 2_000L, path = "/v1/status") })

        assertEquals(1, narrative.anomalies.count { it.startsWith("LOOP") }, narrative.anomalies.toString())
        assertTrue(narrative.anomalies.single { it.startsWith("LOOP") }.contains("60×"), narrative.anomalies.toString())
    }

    @Test
    fun `a crash, an http error and a large response survive the anomaly cap`() {
        val crashes = (0 until 30).map { event("c$it", it * 1_000L, TimelineSource.Crash, "Boom$it") }
        val others = listOf(
            net("e", 40_000, path = "/v1/checkout", status = 500),
            net("big", 41_000, path = "/v1/export", bytes = 9_000_000),
        )

        val narrative = narrate(crashes + others)

        assertEquals(20, narrative.anomalies.size)
        assertTrue(narrative.anomalies.any { it.startsWith("HTTP 500") }, narrative.anomalies.toString())
        assertTrue(narrative.anomalies.any { it.startsWith("LARGE") }, narrative.anomalies.toString())
        assertEquals(32, narrative.anomalyCount)
    }

    @Test
    fun `a folded line shows the largest response it holds`() {
        val narrative = narrate(listOf(net("a", 0, bytes = 100), net("b", 300, bytes = 5_000_000), net("c", 600, bytes = 200)))

        assertEquals(1, narrative.lines.size)
        assertTrue(narrative.lines[0].contains("largest 4.8 MB"), narrative.lines[0])
    }

    @Test
    fun `when truncating, database lines go before the others`() {
        val database = (0 until 5).map { event("d$it", 20_000L + it * 100, TimelineSource.Database, "SELECT $it") }
        val calls = (0 until 3).map { net("n$it", it * 10_000L, path = "/v1/other$it") }

        val narrative = narrate(database + calls, anchorMs = base + 20_000, maxLines = 3)

        assertEquals(listOf("[network:n0]", "[network:n1]", "[network:n2]"), narrative.lines.map { it.substringAfterLast("  ") })
    }

    @Test
    fun `database lines fill the room the others leave, the nearest to the anchor first`() {
        val database = (0 until 5).map { event("d$it", 20_000L + it * 1_000, TimelineSource.Database, "SELECT $it") }
        val calls = listOf(net("n0", 0))

        val narrative = narrate(database + calls, anchorMs = base + 20_000, maxLines = 3)

        assertEquals(listOf("[network:n0]", "[database:d0]", "[database:d1]"), narrative.lines.map { it.substringAfterLast("  ") })
        assertEquals(3, narrative.omittedCount)
    }
}
