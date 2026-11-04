package io.github.openflocon.mcp.timeline

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceUseCase
import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.usecase.RequestDesktopScreenUseCase
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelineUserFilterProvider
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventHiddenReasonsUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventsUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.RequestTimelineFocusUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineGraphUseCase
import io.github.openflocon.mcp.FakeDesktopNavigationRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.FakeTimelineFocusRepository
import io.github.openflocon.mcp.FakeTimelinePreferencesRepository
import io.github.openflocon.mcp.FakeTimelineRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.screen.McpSelectionApplier
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShowInTimelineMcpToolProviderTest {

    private val zone = ZoneId.of("UTC")
    private val base = ZonedDateTime.of(2026, 11, 3, 14, 0, 0, 0, zone).toInstant().toEpochMilli()
    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val tablet = DeviceDomainModel(deviceId = "tablet", deviceName = "Tablet", platform = "android")

    private fun at(seconds: Int) = base + seconds * 1_000L

    private fun event(id: String, seconds: Int, source: TimelineSource, title: String, status: Int? = null) = TimelineEventDomainModel(
        id = id,
        source = source,
        timestamp = at(seconds),
        title = title,
        subtitle = null,
        appInstance = null,
        method = if (source == TimelineSource.Network) "GET" else null,
        statusCode = status,
    )

    // the app started at 0, a crash at 100 s
    private val events = listOf(
        event("0", 0, TimelineSource.AppStart, ""),
        event("n1", 10, TimelineSource.Network, "https://api.shop.io/v1/session", status = 200),
        event("n2", 20, TimelineSource.Network, "https://api.shop.io/v1/feed", status = 200),
        event("a1", 50, TimelineSource.Analytics, "checkout_failed"),
        event("c1", 100, TimelineSource.Crash, "IllegalStateException"),
        event("n5", 200, TimelineSource.Network, "https://api.shop.io/v1/after", status = 200),
    )

    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample")), tablet to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val focusRepository = FakeTimelineFocusRepository()
    private val navigationRepository = FakeDesktopNavigationRepository()
    private val preferences = FakeTimelinePreferencesRepository()
    private var userFilter = TimelineFilterDomainModel()
    private var server: McpTestServer? = null

    private fun start(events: List<TimelineEventDomainModel> = this.events): McpTestServer {
        val timelineRepository = FakeTimelineRepository(events)
        val getEvents = GetTimelineEventsUseCase(timelineRepository)
        return McpTestServer(
            toolProviders = listOf(
                ShowInTimelineMcpToolProvider(
                    targetResolver = targetResolver(devicesRepository),
                    windowFinder = TimelineWindowFinder(getEvents, GetTimelineSummaryUseCase(timelineRepository), zone, nowMs = { at(3_600) }),
                    getTimelineEventsUseCase = getEvents,
                    selectionApplier = McpSelectionApplier(
                        targetResolver = targetResolver(devicesRepository),
                        selectDeviceUseCase = SelectDeviceUseCase(devicesRepository),
                        selectDeviceAppUseCase = SelectDeviceAppUseCase(devicesRepository, GetCurrentDeviceIdUseCase(devicesRepository)),
                    ),
                    setTimelineGraphUseCase = SetTimelineGraphUseCase(preferences),
                    requestTimelineFocusUseCase = RequestTimelineFocusUseCase(focusRepository),
                    requestDesktopScreenUseCase = RequestDesktopScreenUseCase(navigationRepository),
                    getTimelineEventHiddenReasonsUseCase = GetTimelineEventHiddenReasonsUseCase(
                        userFilter = object : TimelineUserFilterProvider {
                            override suspend fun current() = userFilter
                        },
                        getTimelineEventsUseCase = getEvents,
                    ),
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

    private fun assertNothingChanged() {
        assertTrue(devicesRepository.selections.isEmpty(), devicesRepository.selections.toString())
        assertTrue(preferences.graphCalls.isEmpty())
        assertTrue(focusRepository.requests.isEmpty())
        assertTrue(navigationRepository.requestedScreens.isEmpty())
    }

    private fun JsonObject.hiddenBy() = this["hiddenBy"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty()

    private fun JsonObject.message() = this["message"]!!.jsonPrimitive.content

    @Test
    fun `show_in_timeline changes the interface, it is not read only`() = runBlocking {
        val tools = start().listTools()

        assertEquals(listOf("show_in_timeline"), tools.map { it.name })
        assertTrue(tools.single().annotations?.readOnlyHint != true)
    }

    @Test
    fun `without input it shows the crash in graph mode`() = runBlocking {
        val result = start().callTool("show_in_timeline").json()

        val request = focusRepository.requests.single()
        assertEquals(TimelineEventRef(TimelineSource.Crash, "c1"), request.event)
        assertEquals(at(40), request.fromMs)
        assertEquals(at(105), request.toMs)
        assertEquals(at(0), request.sessionStart)
        assertEquals("pixel", request.target.deviceId)
        assertEquals(listOf(true), preferences.graphCalls)
        assertEquals(listOf(FloconScreen.Timeline), navigationRepository.requestedScreens)
        assertEquals("crash:c1", result["eventId"]!!.jsonPrimitive.content)
    }

    @Test
    fun `an event id focuses that event with the seconds around it`() = runBlocking {
        start().callTool("show_in_timeline", mapOf("eventId" to "network:n2", "aroundSeconds" to 5)).json()

        val request = focusRepository.requests.single()
        assertEquals(TimelineEventRef(TimelineSource.Network, "n2"), request.event)
        assertEquals(at(15), request.fromMs)
        assertEquals(at(25), request.toMs)
    }

    @Test
    fun `a time without anomaly there shows the zone only`() = runBlocking {
        val result = start().callTool("show_in_timeline", mapOf("at" to at(50).toString(), "aroundSeconds" to 10)).json()

        val request = focusRepository.requests.single()
        assertNull(request.event)
        assertEquals(at(40), request.fromMs)
        assertEquals(at(60), request.toMs)
        assertNull(result["eventId"])
    }

    @Test
    fun `list view says only the row is selected and does not turn the graph on`() = runBlocking {
        val result = start().callTool("show_in_timeline", mapOf("view" to "list")).json()

        assertEquals(listOf(false), preferences.graphCalls)
        assertTrue(result.message().contains("list"), result.message())
        assertTrue(result.message().contains("scroll"), result.message())
    }

    @Test
    fun `the message tells the user to go to the Flocon app and that the request is registered`() = runBlocking {
        val result = start().callTool("show_in_timeline").json()

        assertTrue(result.message().contains("go to the Flocon"), result.message())
        assertTrue(result.message().contains("registered"), result.message())
        assertTrue(result.message().contains("not brought to the front"), result.message())
    }

    @Test
    fun `another device is selected before the focus is requested`() = runBlocking {
        start().callTool("show_in_timeline", mapOf("deviceId" to "tablet", "packageName" to "io.flocon.sample")).json()

        assertTrue(devicesRepository.selections.contains("device:tablet"), devicesRepository.selections.toString())
        assertEquals("tablet", focusRepository.requests.single().target.deviceId)
    }

    @Test
    fun `an unknown event changes nothing`() = runBlocking {
        val result = start().callTool("show_in_timeline", mapOf("eventId" to "network:zzz"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("zzz"))
        assertNothingChanged()
    }

    @Test
    fun `a window without events changes nothing`() = runBlocking {
        val result = start().callTool("show_in_timeline", mapOf("mode" to "at", "at" to "03:00"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("No event"), result.text())
        assertNothingChanged()
    }

    @Test
    fun `an unknown view changes nothing`() = runBlocking {
        val result = start().callTool("show_in_timeline", mapOf("view" to "grid"))

        assertEquals(true, result.isError)
        assertNothingChanged()
    }

    @Test
    fun `an event hidden by the filters of the user is reported with the reason, and the filters are untouched`() = runBlocking {
        userFilter = TimelineFilterDomainModel(sources = TimelineSource.entries.toSet() - TimelineSource.Crash)

        val result = start().callTool("show_in_timeline").json()

        assertTrue(result.hiddenBy().single().contains("source"), result.hiddenBy().toString())
        assertTrue(result.message().contains("filters were not changed"), result.message())
        assertEquals(TimelineSource.entries.toSet() - TimelineSource.Crash, userFilter.sources)
        // the zone is shown anyway
        assertEquals(1, focusRepository.requests.size)
    }

    @Test
    fun `an event shown by the filters has no hidden reason`() = runBlocking {
        val result = start().callTool("show_in_timeline").json()

        assertTrue(result.hiddenBy().isEmpty())
    }
}
