package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocOpener
import io.github.openflocon.flocondesktop.common.ui.featuredoc.HelpSource
import io.github.openflocon.flocondesktop.common.ui.featuredoc.helpScreenName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TelemetryUsageTest {

    private val snakeCase = Regex("[a-z]+(_[a-z]+)*")

    @Test
    fun `wire names are unique and snake case`() {
        val features = TelemetryFeature.entries.map { it.wireName }
        val settings = TelemetrySetting.entries.map { it.wireName }
        assertEquals(features.size, features.toSet().size)
        assertEquals(settings.size, settings.toSet().size)
        (features + settings + HelpSource.entries.map { it.wireName }).forEach {
            assertTrue(snakeCase.matches(it), "$it is not snake_case")
        }
    }

    @Test
    fun `every feature and setting has a screen, sent with the event`() {
        val client = FakeTelemetryRepository()
        val manager = telemetryManager(client)

        TelemetryFeature.entries.forEach { manager.featureUsed(it) }
        TelemetrySetting.entries.forEach { manager.settingChanged(it, true) }

        assertEquals(TelemetryFeature.entries.size + TelemetrySetting.entries.size, client.events.size)
        val screens = TelemetryScreen.entries.map { it.wireName }.toSet()
        client.events.forEach { (_, properties) -> assertTrue(properties["screen"] in screens) }
    }

    @Test
    fun `feature used carries the feature, screen, bounded value and source`() {
        val client = FakeTelemetryRepository()
        val manager = telemetryManager(client)

        manager.featureUsed(TelemetryFeature.MockToggled, value = true, enabledMocksCount = 2)
        manager.featureUsed(TelemetryFeature.DetailCopyCurl, value = "https://secret")
        manager.featureUsed(TelemetryFeature.HelpOpened, screen = "Network", source = HelpSource.EmptyState.wireName)

        val (event, toggled) = client.events[0]
        assertEquals(TelemetryEvents.FEATURE_USED, event)
        assertEquals("mock_toggled", toggled["feature"])
        assertEquals("NetworkMocks", toggled["screen"])
        assertEquals(true, toggled["value"])
        assertEquals(2, toggled["enabled_mocks_count"])
        // a String value is never sent for a feature
        assertEquals("NetworkDetail", client.events[1].second["screen"])
        assertEquals(false, "value" in client.events[1].second)
        val help = client.events[2].second
        assertEquals("Network", help["screen"])
        assertEquals("empty_state", help["source"])
    }

    @Test
    fun `setting changed sends the setting and value`() {
        val client = FakeTelemetryRepository()
        telemetryManager(client).settingChanged(TelemetrySetting.TimelineView, "graph")

        val (event, properties) = client.events.single()
        assertEquals(TelemetryEvents.SETTING_CHANGED, event)
        assertEquals("timeline_view", properties["setting"])
        assertEquals("graph", properties["value"])
        assertEquals("Timeline", properties["screen"])
    }

    @Test
    fun `a screen is sent once per launch, menu screens included`() {
        val client = FakeTelemetryRepository()
        val manager = telemetryManager(client)

        repeat(3) { manager.screenViewed(TelemetryScreen.NetworkDetail) }
        repeat(3) { manager.screenViewed("Database") }
        manager.screenViewed("Network")

        assertEquals(listOf("NetworkDetail", "Database", "Network"), client.events.map { it.second["screen"] })
        assertTrue(client.events.all { it.first == TelemetryEvents.SCREEN_VIEWED })
    }

    @Test
    fun `once per launch is not consumed before the user opted in`() {
        val client = FakeTelemetryRepository(level = null)
        val manager = telemetryManager(client)

        manager.screenViewed(TelemetryScreen.NetworkDetail)
        manager.featureUsed(TelemetryFeature.DatabaseQueryExecuted)
        manager.emptyStateShown("Network")
        manager.mcpUsed("list_network_calls", "claude")
        assertTrue(client.events.isEmpty())

        manager.setLevel(TelemetryLevelDomainModel.Anonymous)
        manager.screenViewed(TelemetryScreen.NetworkDetail)
        manager.featureUsed(TelemetryFeature.DatabaseQueryExecuted)
        manager.emptyStateShown("Network")
        manager.mcpUsed("list_network_calls", "claude")
        assertEquals(4, client.events.size)
    }

    @Test
    fun `a core action is sent once per launch, with its screen and nothing else`() {
        val client = FakeTelemetryRepository()
        val manager = telemetryManager(client)

        repeat(3) { manager.featureUsed(TelemetryFeature.DatabaseQueryExecuted) }
        repeat(2) { manager.featureUsed(TelemetryFeature.DeeplinkLaunched) }
        // a regular feature is not deduplicated
        repeat(2) { manager.featureUsed(TelemetryFeature.MockToggled, value = true) }

        assertEquals(4, client.events.size)
        val (event, properties) = client.events.first()
        assertEquals(TelemetryEvents.FEATURE_USED, event)
        assertEquals("database_query_executed", properties["feature"])
        assertEquals(mapOf<String, Any>("feature" to "database_query_executed", "screen" to "Database"), properties)
    }

    @Test
    fun `core action wire names`() {
        val once = TelemetryFeature.entries.filter { it.oncePerLaunch }.associate { it.wireName to it.screen.wireName }
        assertEquals(
            mapOf(
                "database_query_executed" to "Database",
                "deeplink_launched" to "Deeplinks",
                "shared_preference_edited" to "SharedPreferences",
                "file_opened" to "Files",
                "adb_command_sent" to "AdbCommander",
                "screenshot_taken" to "TopBar",
                "video_recorded" to "TopBar",
            ),
            once,
        )
    }

    @Test
    fun `empty state is sent once per page with the page name only`() {
        val client = FakeTelemetryRepository()
        val manager = telemetryManager(client)

        repeat(3) { manager.emptyStateShown(FeatureDoc.Network.name) }
        manager.emptyStateShown(FeatureDoc.entries.first { it != FeatureDoc.Network }.name)

        assertEquals(2, client.events.size)
        val (event, properties) = client.events.first()
        assertEquals(TelemetryEvents.SCREEN_EMPTY_SHOWN, event)
        assertEquals(mapOf<String, Any>("screen" to "Network"), properties)
    }

    @Test
    fun `mcp usage is sent once per tool with the tool and the client only`() {
        val client = FakeTelemetryRepository()
        val manager = telemetryManager(client)

        repeat(3) { manager.mcpUsed("list_network_calls", "claude") }
        manager.mcpUsed("get_network_call", "claude")

        assertEquals(2, client.events.size)
        val (event, properties) = client.events.first()
        assertEquals(TelemetryEvents.MCP_USED, event)
        assertEquals(mapOf<String, Any>("tool" to "list_network_calls", "client" to "claude"), properties)
    }

    @Test
    fun `list_devices is never sent as an mcp usage`() {
        val client = FakeTelemetryRepository()
        val manager = telemetryManager(client)

        manager.mcpUsed("list_devices", "claude")

        assertTrue(client.events.isEmpty())
    }

    @Test
    fun `every documentation page maps to a screen name`() {
        FeatureDoc.entries.forEach { assertEquals(it.name, it.helpScreenName) }
        assertEquals("Setup", (null as FeatureDoc?).helpScreenName)
    }

    @Test
    fun `an opener receives the doc, url and source`() {
        val calls = mutableListOf<Triple<FeatureDoc?, String, HelpSource>>()
        val opener = FeatureDocOpener { doc, url, source -> calls += Triple(doc, url, source) }

        opener.open(FeatureDoc.Network, FeatureDoc.Network.url, HelpSource.TopBar)
        opener.open(null, FeatureDoc.SETUP_URL, HelpSource.EmptyStateNoDevice)

        assertEquals(FeatureDoc.Network, calls[0].first)
        assertEquals("Setup", calls[1].first.helpScreenName)
        assertEquals(HelpSource.EmptyStateNoDevice, calls[1].third)
    }
}
