package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class AppStartedTelemetryTest {

    private class FakeClient : TelemetryClient {
        val events = mutableListOf<Pair<String, Map<String, Any>>>()
        override suspend fun capture(event: String, distinctId: String, properties: Map<String, Any>) {
            events += event to properties
        }
    }

    private class FakePreferences(override var level: TelemetryLevel? = TelemetryLevel.Anonymous) : TelemetryPreferences {
        override var installationId: String? = null
    }

    private fun manager(client: TelemetryClient, level: TelemetryLevel? = TelemetryLevel.Anonymous) = TelemetryManager(
        preferences = FakePreferences(level),
        client = client,
        applicationScope = CoroutineScope(Dispatchers.Unconfined),
        config = TelemetryConfig(apiKey = "phc_test", host = "https://eu.i.posthog.com", appVersion = "1.0.0", doNotTrack = false),
        osName = "TestOS",
        osArch = "test64",
    )

    private fun telemetry(
        manager: TelemetryManager,
        timeline: suspend () -> TimelineSettingsDomainModel = {
            TimelineSettingsDomainModel(graph = false, autoScroll = true, screenshots = true, mergePlugins = false)
        },
        mocks: suspend () -> Int = { 3 },
    ) = AppStartedTelemetry(
        telemetryManager = manager,
        timelineSettings = timeline,
        mascotEnabled = { false },
        enabledMocksCount = mocks,
        badNetworkEnabled = { true },
        readTimeout = 100.milliseconds,
    )

    @Test
    fun `sends the snapshot of the settings`() = runBlocking {
        val client = FakeClient()

        telemetry(manager(client)).send()

        val (event, properties) = client.events.single()
        assertEquals(TelemetryEvents.APP_STARTED, event)
        assertEquals("list", properties["timeline_view"])
        assertEquals(true, properties["timeline_auto_scroll"])
        assertEquals(true, properties["timeline_screenshots"])
        assertEquals(false, properties["timeline_merge_plugins"])
        assertEquals(false, properties["mascot_enabled"])
        assertEquals(3, properties["enabled_mocks_count"])
        assertEquals(true, properties["bad_network_enabled"])
    }

    @Test
    fun `a failing or slow source is left out`() = runBlocking {
        val client = FakeClient()

        telemetry(
            manager(client),
            timeline = { error("boom") },
            mocks = { awaitCancellation() },
        ).send()

        val properties = client.events.single().second
        assertFalse("timeline_view" in properties)
        assertFalse("enabled_mocks_count" in properties)
        assertEquals(false, properties["mascot_enabled"])
        assertEquals(true, properties["bad_network_enabled"])
    }

    @Test
    fun `nothing is sent when not opted in`() = runBlocking {
        val client = FakeClient()

        telemetry(manager(client, level = null)).send()

        assertTrue(client.events.isEmpty())
    }

    @Test
    fun `is sent once per launch`() = runBlocking {
        val client = FakeClient()
        val telemetry = telemetry(manager(client))

        telemetry.send()
        telemetry.send()

        assertEquals(1, client.events.size)
    }
}
