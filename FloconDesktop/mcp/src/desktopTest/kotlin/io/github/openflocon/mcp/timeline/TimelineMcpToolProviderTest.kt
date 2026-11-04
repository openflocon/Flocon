package io.github.openflocon.mcp.timeline

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.timeline.models.TIMELINE_MAX_DATABASE_LOGS
import io.github.openflocon.domain.timeline.models.TIMELINE_MAX_EVENTS
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventsUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineSummaryUseCase
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.FakeTimelineRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimelineMcpToolProviderTest {

    private val zone = ZoneId.of("UTC")
    private val base = ZonedDateTime.of(2026, 11, 3, 14, 0, 0, 0, zone).toInstant().toEpochMilli()
    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")

    private fun at(seconds: Int) = base + seconds * 1_000L

    private fun event(id: String, seconds: Int, source: TimelineSource, title: String, subtitle: String? = null, method: String? = null, status: Int? = null) = TimelineEventDomainModel(
        id = id,
        source = source,
        timestamp = at(seconds),
        title = title,
        subtitle = subtitle,
        appInstance = null,
        method = method,
        statusCode = status,
    )

    private fun net(id: String, seconds: Int, path: String, status: Int = 200) = event(id, seconds, TimelineSource.Network, "https://api.shop.io$path", method = "GET", status = status)

    // the app started at 0, a crash at 100 s, and a call after it
    private val events = listOf(
        event("0", 0, TimelineSource.AppStart, ""),
        net("n1", 10, "/v1/session"),
        net("n2", 20, "/v1/feed"),
        net("n3", 30, "/v1/profile"),
        event("a1", 50, TimelineSource.Analytics, "checkout_failed", "Firebase"),
        net("n4", 90, "/v1/cart", status = 500),
        event("c1", 100, TimelineSource.Crash, "IllegalStateException", "bad state"),
        net("n5", 200, "/v1/after"),
    )

    private var server: McpTestServer? = null

    private fun start(events: List<TimelineEventDomainModel> = this.events, withDevice: Boolean = true): McpTestServer {
        val devicesRepository = FakeDevicesRepository(
            devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
            currentDeviceIdValue = if (withDevice) "pixel" else null,
            selectedApps = if (withDevice) mapOf("pixel" to "io.flocon.sample") else emptyMap(),
        )
        val repository = FakeTimelineRepository(events)
        return McpTestServer(
            toolProviders = listOf(
                TimelineMcpToolProvider(
                    targetResolver = targetResolver(devicesRepository),
                    windowFinder = TimelineWindowFinder(
                        getTimelineEventsUseCase = GetTimelineEventsUseCase(repository),
                        getTimelineSummaryUseCase = GetTimelineSummaryUseCase(repository),
                        zone = zone,
                        nowMs = { at(3_600) },
                    ),
                    getTimelineEventsUseCase = GetTimelineEventsUseCase(repository),
                    zone = zone,
                ),
            ),
        ).also {
            runBlocking { it.start() }
            server = it
        }
    }

    @AfterTest
    fun tearDown() = runBlocking { server?.stop() ?: Unit }

    private fun JsonObject.lines() = this["lines"]!!.jsonArray.map { it.jsonPrimitive.content }

    private fun JsonObject.anomalies() = this["anomalies"]!!.jsonArray.map { it.jsonPrimitive.content }

    private fun JsonObject.window() = this["window"]!!.jsonObject

    @Test
    fun `get_timeline is read only`() = runBlocking {
        val tools = start().listTools()

        assertEquals(listOf("get_timeline"), tools.map { it.name })
        assertTrue(tools.single().annotations?.readOnlyHint == true)
    }

    @Test
    fun `get_timeline without input anchors on the crash`() = runBlocking {
        val result = start().callTool("get_timeline").json()

        val lines = result.lines()
        // the minute before the crash and the 5 seconds after: the calls at 10, 20, 30 s and 200 s are out
        assertTrue(lines.any { it.contains("checkout_failed") }, lines.toString())
        assertTrue(lines.any { it.contains("/v1/cart") }, lines.toString())
        assertTrue(lines.any { it.contains("IllegalStateException") }, lines.toString())
        assertTrue(lines.none { it.contains("/v1/feed") || it.contains("/v1/after") }, lines.toString())
        assertTrue(result.anomalies().any { it.startsWith("CRASH") }, result.anomalies().toString())
        assertTrue(result.anomalies().any { it.startsWith("HTTP 500") }, result.anomalies().toString())
        assertNull(result["found"])
    }

    @Test
    fun `the answer carries the window and the reason`() = runBlocking {
        val window = start().callTool("get_timeline").json().window()

        assertEquals(at(40), window["fromMs"]!!.jsonPrimitive.content.toLong())
        assertEquals(at(105), window["toMs"]!!.jsonPrimitive.content.toLong())
        assertEquals("UTC", window["timeZone"]!!.jsonPrimitive.content)
        assertTrue(window["fromLocal"]!!.jsonPrimitive.content.startsWith("2026-11-03T14:00:40"), window.toString())
        assertTrue(window["reason"]!!.jsonPrimitive.content.contains("IllegalStateException"), window.toString())
    }

    @Test
    fun `eventId anchors the window around that event`() = runBlocking {
        val result = start().callTool("get_timeline", mapOf("eventId" to "network:n2", "aroundSeconds" to 5)).json()

        assertEquals(1, result.lines().size, result.lines().toString())
        assertTrue(result.lines().single().contains("/v1/feed"))
        assertTrue(result.window()["reason"]!!.jsonPrimitive.content.contains("Around"))
    }

    @Test
    fun `an unknown eventId is an error that names it`() = runBlocking {
        val result = start().callTool("get_timeline", mapOf("eventId" to "network:zzz"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("zzz"))
    }

    @Test
    fun `search answers whether an event was sent`() = runBlocking {
        val found = start().callTool("get_timeline", mapOf("search" to "checkout_failed")).json()
        val missing = start().callTool("get_timeline", mapOf("search" to "never_sent")).json()

        assertEquals(true, found["found"]!!.jsonPrimitive.boolean)
        assertEquals(1, found["searchMatches"]!!.jsonArray.size)
        assertEquals(false, missing["found"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `search matches are kept when the lines are capped`() = runBlocking {
        val result = start().callTool("get_timeline", mapOf("search" to "checkout_failed", "maxLines" to 1)).json()

        assertEquals(1, result.lines().size)
        assertEquals(true, result["truncated"]!!.jsonPrimitive.boolean)
        assertEquals(true, result["found"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `the search can be limited to a source, an unknown source lists the valid ones`() = runBlocking {
        val server = start()
        val network = server.callTool("get_timeline", mapOf("search" to "checkout", "searchSource" to "network")).json()
        val error = server.callTool("get_timeline", mapOf("search" to "checkout", "searchSource" to "nope"))

        assertEquals(false, network["found"]!!.jsonPrimitive.boolean)
        assertEquals(true, error.isError)
        assertTrue(error.text().contains("analytics"))
    }

    @Test
    fun `a time with an unreadable format lists the accepted ones`() = runBlocking {
        val result = start().callTool("get_timeline", mapOf("mode" to "at", "at" to "soon"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("HH:mm"))
    }

    @Test
    fun `no event gives an empty narrative that says why`() = runBlocking {
        val result = start(events = emptyList()).callTool("get_timeline").json()

        assertEquals(0, result["eventCount"]!!.jsonPrimitive.int)
        assertTrue(result.lines().isEmpty())
        assertTrue(result.window()["reason"]!!.jsonPrimitive.content.contains("No event"))
    }

    @Test
    fun `an unknown device lists the valid pairs`() = runBlocking {
        val result = start().callTool("get_timeline", mapOf("deviceId" to "nope"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("pixel"), result.text())
    }

    @Test
    fun `no selected device gives a clear message`() = runBlocking {
        val result = start(withDevice = false).callTool("get_timeline")

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("selected"), result.text())
    }

    @Test
    fun `a window holding the most database logs the app keeps says they may be partial`() = runBlocking {
        val logs = (0 until TIMELINE_MAX_DATABASE_LOGS).map { event("d$it", 99, TimelineSource.Database, "SELECT 1") }

        val result = start(events = events + logs).callTool("get_timeline").json()

        assertTrue(result["notes"]!!.jsonArray.any { it.jsonPrimitive.content.contains("database", ignoreCase = true) }, result.toString())
        assertFalse(result["notes"]!!.jsonArray.isEmpty())
    }

    @Test
    fun `the notes tell the times are those of the phone`() = runBlocking {
        val result = start().callTool("get_timeline").json()

        assertTrue(result["notes"]!!.jsonArray.any { it.jsonPrimitive.content.contains("phone") }, result.toString())
    }

    private fun JsonObject.notes() = this["notes"]!!.jsonArray.map { it.jsonPrimitive.content }

    @Test
    fun `database logs older than the ones the app keeps are reported even when the window holds none`() = runBlocking {
        // the app keeps only its newest database logs: a window before all of them finds none, which does not mean there was none
        val logs = (0 until TIMELINE_MAX_DATABASE_LOGS).map { event("d$it", 150, TimelineSource.Database, "SELECT 1") }

        val result = start(events = events + logs).callTool("get_timeline").json()

        assertTrue(result.notes().any { it.contains("database", ignoreCase = true) && it.contains("older") }, result.notes().toString())
    }

    @Test
    fun `a window that hits the cap of events says the oldest are missing`() = runBlocking {
        val many = (0 until TIMELINE_MAX_EVENTS).map { net("m$it", 99, "/v1/spam") }

        val result = start(events = events + many).callTool("get_timeline").json()

        assertTrue(result.notes().any { it.contains("$TIMELINE_MAX_EVENTS") && it.contains("oldest") }, result.notes().toString())
    }

    @Test
    fun `a window without event tells where the events of the app are`() = runBlocking {
        val result = start().callTool("get_timeline", mapOf("mode" to "at", "at" to "03:00")).json()

        assertEquals(0, result["eventCount"]!!.jsonPrimitive.int)
        assertTrue(result.notes().any { it.startsWith("No event in this window") && it.contains("14:00:00") }, result.notes().toString())
    }

    @Test
    fun `excludeSources leaves a source out of the window`() = runBlocking {
        val logs = (0 until 5).map { event("d$it", 99, TimelineSource.Database, "SELECT $it") }
        val server = start(events = events + logs)

        val all = server.callTool("get_timeline").json()
        val withoutDatabase = server.callTool("get_timeline", mapOf("excludeSources" to listOf("database"))).json()

        assertTrue(all.lines().any { it.contains("  DB  ") })
        assertTrue(withoutDatabase.lines().none { it.contains("  DB  ") }, withoutDatabase.lines().toString())
        assertEquals(all["eventCount"]!!.jsonPrimitive.int - 5, withoutDatabase["eventCount"]!!.jsonPrimitive.int)
    }

    @Test
    fun `sources keeps only the listed ones`() = runBlocking {
        val result = start().callTool("get_timeline", mapOf("sources" to listOf("analytics"))).json()

        assertEquals(1, result.lines().size, result.lines().toString())
        assertTrue(result.lines().single().contains("checkout_failed"))
    }

    @Test
    fun `an unknown source in sources lists the valid ones`() = runBlocking {
        val result = start().callTool("get_timeline", mapOf("sources" to listOf("nope")))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("analytics"), result.text())
    }
}
