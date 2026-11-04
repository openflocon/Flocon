package io.github.openflocon.mcp.screen

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceUseCase
import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.usecase.RequestDesktopScreenUseCase
import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import io.github.openflocon.domain.timeline.usecase.SetTimelineGraphUseCase
import io.github.openflocon.mcp.FakeDesktopNavigationRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NavigateDesktopAppMcpToolProviderTest {

    private val toolName = "flocon_navigate_into_desktop_app"

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val iphone = DeviceDomainModel(deviceId = "iphone", deviceName = "iPhone 16", platform = "ios")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(
            pixel to listOf(app("io.flocon.sample"), app("io.flocon.other")),
            iphone to listOf(app("io.flocon.ios")),
        ),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val desktopNavigationRepository = FakeDesktopNavigationRepository()
    private val timelinePreferences = FakeTimelinePreferencesRepository()

    private val server = McpTestServer(
        toolProviders = listOf(
            NavigateDesktopAppMcpToolProvider(
                targetResolver = targetResolver(devicesRepository),
                selectionApplier = McpSelectionApplier(
                    targetResolver = targetResolver(devicesRepository),
                    selectDeviceUseCase = SelectDeviceUseCase(devicesRepository),
                    selectDeviceAppUseCase = SelectDeviceAppUseCase(devicesRepository, GetCurrentDeviceIdUseCase(devicesRepository)),
                ),
                setTimelineGraphUseCase = SetTimelineGraphUseCase(timelinePreferences),
                requestDesktopScreenUseCase = RequestDesktopScreenUseCase(desktopNavigationRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `the tool is not read only and is idempotent`() = runBlocking {
        val tool = server.listTools().single { it.name == toolName }

        assertEquals(false, tool.annotations?.readOnlyHint)
        assertEquals(true, tool.annotations?.idempotentHint)
    }

    @Test
    fun `without any parameter the tool answers an error and changes nothing`() = runBlocking {
        val result = server.callTool(toolName)

        assertEquals(true, result.isError)
        assertNothingChanged()
    }

    @Test
    fun `screen requests the screen in the store`() = runBlocking {
        val result = server.callTool(toolName, mapOf("screen" to "feature_flags")).json()

        assertEquals(FloconScreen.FeatureFlags, desktopNavigationRepository.requestedScreens.lastOrNull())
        assertTrue(result["applied"]!!.jsonArray.isNotEmpty())
    }

    @Test
    fun `every screen can be requested by its snake case name`() = runBlocking {
        val names = mapOf(
            "adb_commander" to FloconScreen.AdbCommander,
            "shared_preferences" to FloconScreen.SharedPreferences,
            "timeline" to FloconScreen.Timeline,
        )
        names.forEach { (name, screen) ->
            server.callTool(toolName, mapOf("screen" to name)).json()

            assertEquals(screen, desktopNavigationRepository.requestedScreens.lastOrNull(), name)
        }
    }

    @Test
    fun `an unknown screen is an error listing the valid ones`() = runBlocking {
        val result = server.callTool(toolName, mapOf("screen" to "nope"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("feature_flags"))
        assertNothingChanged()
    }

    @Test
    fun `settings cannot be requested`() = runBlocking {
        val result = server.callTool(toolName, mapOf("screen" to "settings"))

        assertEquals(true, result.isError)
        assertTrue(desktopNavigationRepository.requestedScreens.isEmpty())
    }

    @Test
    fun `timelineView graph sets the graph mode, list unsets it`() = runBlocking {
        server.callTool(toolName, mapOf("timelineView" to "list")).json()
        assertEquals(listOf(false), timelinePreferences.graphCalls)

        server.callTool(toolName, mapOf("timelineView" to "graph")).json()
        assertEquals(listOf(false, true), timelinePreferences.graphCalls)
    }

    @Test
    fun `an invalid timelineView is an error and changes nothing`() = runBlocking {
        val result = server.callTool(toolName, mapOf("timelineView" to "table", "screen" to "network"))

        assertEquals(true, result.isError)
        assertNothingChanged()
    }

    @Test
    fun `deviceId and packageName select that device then that app`() = runBlocking {
        server.callTool(toolName, mapOf("deviceId" to "iphone", "packageName" to "io.flocon.ios")).json()

        assertEquals(listOf("device:iphone", "app:iphone/io.flocon.ios"), devicesRepository.selections)
    }

    @Test
    fun `a device alone selects only that device`() = runBlocking {
        server.callTool(toolName, mapOf("deviceId" to "iphone")).json()

        assertEquals(listOf("device:iphone"), devicesRepository.selections)
    }

    @Test
    fun `packageName alone selects the app of the current device without changing device`() = runBlocking {
        server.callTool(toolName, mapOf("packageName" to "io.flocon.other")).json()

        assertEquals(listOf("app:pixel/io.flocon.other"), devicesRepository.selections)
    }

    @Test
    fun `packageName alone selects the app and its device if it is on another one`() = runBlocking {
        server.callTool(toolName, mapOf("packageName" to "io.flocon.ios")).json()

        assertEquals(listOf("device:iphone", "app:iphone/io.flocon.ios"), devicesRepository.selections)
    }

    @Test
    fun `an unknown device or app is an error and nothing else is applied`() = runBlocking {
        val result = server.callTool(
            toolName,
            mapOf("deviceId" to "nope", "screen" to "network", "timelineView" to "graph"),
        )

        assertEquals(true, result.isError)
        assertNothingChanged()
    }

    @Test
    fun `the answer lists what was applied and tells the user to go to Flocon`() = runBlocking {
        val result = server.callTool(
            toolName,
            mapOf("screen" to "timeline", "timelineView" to "graph", "deviceId" to "iphone"),
        ).json()

        assertEquals(3, result["applied"]!!.jsonArray.size)
        assertTrue(result["message"]!!.jsonPrimitive.content.contains("Flocon"))
    }

    private fun assertNothingChanged() {
        assertTrue(desktopNavigationRepository.requestedScreens.isEmpty())
        assertTrue(devicesRepository.selections.isEmpty())
        assertTrue(timelinePreferences.graphCalls.isEmpty())
    }
}

private class FakeTimelinePreferencesRepository : TimelinePreferencesRepository {
    val graphCalls = mutableListOf<Boolean>()

    override val settings: Flow<TimelineSettingsDomainModel> = MutableStateFlow(TimelineSettingsDomainModel())

    override suspend fun setGraph(graph: Boolean) {
        graphCalls += graph
    }

    override suspend fun setAutoScroll(autoScroll: Boolean) = error("not used")
    override suspend fun setInvertList(invertList: Boolean) = error("not used")
    override suspend fun setPinPanel(pinPanel: Boolean) = error("not used")
    override suspend fun setSelectedSources(sources: Set<TimelineSource>) = error("not used")
    override suspend fun setScreenshots(screenshots: Boolean) = error("not used")
    override suspend fun setScreenshotIntervalSeconds(seconds: Int) = error("not used")
}
