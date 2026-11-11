package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocOpener
import io.github.openflocon.flocondesktop.common.ui.featuredoc.HelpSource
import io.github.openflocon.flocondesktop.common.ui.featuredoc.helpScreenName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TelemetryUsageTest {

    private val snakeCase = Regex("[a-z]+(_[a-z]+)*")

    private class FakeClient : TelemetryClient {
        val events = mutableListOf<Pair<String, Map<String, Any>>>()
        override suspend fun capture(event: String, distinctId: String, properties: Map<String, Any>) {
            events += event to properties
        }
    }

    private class FakePreferences(override var level: TelemetryLevel? = TelemetryLevel.Anonymous) : TelemetryPreferences {
        override var installationId: String? = null
    }

    private fun manager(client: TelemetryClient, preferences: TelemetryPreferences = FakePreferences()) = TelemetryManager(
        preferences = preferences,
        client = client,
        applicationScope = CoroutineScope(Dispatchers.Unconfined),
        config = TelemetryConfig(apiKey = "phc_test", host = "https://eu.i.posthog.com", appVersion = "1.0.0", doNotTrack = false),
        osName = "TestOS",
        osArch = "test64",
    )

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
        val client = FakeClient()
        val manager = manager(client)

        TelemetryFeature.entries.forEach { manager.featureUsed(it) }
        TelemetrySetting.entries.forEach { manager.settingChanged(it, true) }

        assertEquals(TelemetryFeature.entries.size + TelemetrySetting.entries.size, client.events.size)
        val screens = TelemetryScreen.entries.map { it.wireName }.toSet()
        client.events.forEach { (_, properties) -> assertTrue(properties["screen"] in screens) }
    }

    @Test
    fun `feature used carries the feature, screen, bounded value and source`() {
        val client = FakeClient()
        val manager = manager(client)

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
        assertEquals(true, "value" in client.events[1].second)
        val help = client.events[2].second
        assertEquals("Network", help["screen"])
        assertEquals("empty_state", help["source"])
    }

    @Test
    fun `setting changed sends the setting and value`() {
        val client = FakeClient()
        manager(client).settingChanged(TelemetrySetting.TimelineView, "graph")

        val (event, properties) = client.events.single()
        assertEquals(TelemetryEvents.SETTING_CHANGED, event)
        assertEquals("timeline_view", properties["setting"])
        assertEquals("graph", properties["value"])
        assertEquals("Timeline", properties["screen"])
    }

    @Test
    fun `screen once per launch is sent once`() {
        val client = FakeClient()
        val manager = manager(client)

        repeat(3) { manager.trackScreenOncePerLaunch(TelemetryScreen.NetworkDetail) }

        assertEquals(1, client.events.size)
        assertEquals("NetworkDetail", client.events.single().second["screen"])
    }

    @Test
    fun `screen once per launch is not consumed before the user opted in`() {
        val client = FakeClient()
        val preferences = FakePreferences(level = null)
        val manager = manager(client, preferences)

        manager.trackScreenOncePerLaunch(TelemetryScreen.NetworkDetail)
        assertTrue(client.events.isEmpty())

        manager.setLevel(TelemetryLevel.Anonymous)
        manager.trackScreenOncePerLaunch(TelemetryScreen.NetworkDetail)
        assertEquals(1, client.events.size)
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
