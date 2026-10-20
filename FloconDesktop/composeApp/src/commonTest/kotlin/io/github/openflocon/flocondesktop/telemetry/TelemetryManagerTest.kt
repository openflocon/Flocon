package io.github.openflocon.flocondesktop.telemetry

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TelemetryManagerTest {

    private class FakePreferences(
        override var level: TelemetryLevel? = null,
        override var installationId: String? = null,
    ) : TelemetryPreferences

    private data class Captured(val event: String, val distinctId: String, val properties: Map<String, Any>)

    private class FakeClient : TelemetryClient {
        val events = mutableListOf<Captured>()
        override suspend fun capture(event: String, distinctId: String, properties: Map<String, Any>) {
            events += Captured(event, distinctId, properties)
        }
    }

    private fun config(apiKey: String = "phc_test", doNotTrack: Boolean = false) = TelemetryConfig(
        apiKey = apiKey,
        host = "https://eu.i.posthog.com",
        appVersion = "1.0.0",
        doNotTrack = doNotTrack,
    )

    private fun manager(
        preferences: TelemetryPreferences,
        client: TelemetryClient,
        config: TelemetryConfig = config(),
    ) = TelemetryManager(
        preferences = preferences,
        client = client,
        applicationScope = CoroutineScope(Dispatchers.Unconfined),
        config = config,
        osName = "TestOS",
        osArch = "test64",
    )

    @Test
    fun `build without api key never sends and never asks consent`() {
        val client = FakeClient()
        val manager = manager(FakePreferences(), client, config(apiKey = ""))

        manager.track(TelemetryEvents.APP_STARTED)

        assertTrue(client.events.isEmpty())
        assertFalse(manager.state.value.shouldAskConsent)
        assertEquals(TelemetryLevel.Disabled, manager.state.value.level)
    }

    @Test
    fun `do not track disables everything`() {
        val client = FakeClient()
        val manager = manager(FakePreferences(level = TelemetryLevel.Full), client, config(doNotTrack = true))

        manager.track(TelemetryEvents.APP_STARTED)

        assertTrue(client.events.isEmpty())
        assertFalse(manager.state.value.shouldAskConsent)
    }

    @Test
    fun `undecided user is anonymous and is asked for consent`() {
        val preferences = FakePreferences()
        val client = FakeClient()
        val manager = manager(preferences, client)

        manager.track(TelemetryEvents.APP_STARTED)

        assertTrue(manager.state.value.shouldAskConsent)
        assertEquals(TelemetryLevel.Anonymous, manager.state.value.level)
        assertEquals(1, client.events.size)
        assertNull(preferences.installationId, "anonymous mode must not persist any identifier")
    }

    @Test
    fun `anonymous mode uses the session id`() {
        val client = FakeClient()
        val manager = manager(FakePreferences(level = TelemetryLevel.Anonymous), client)

        manager.track(TelemetryEvents.APP_STARTED)
        manager.track(TelemetryEvents.SCREEN_VIEWED, mapOf("screen" to "Network"))

        val (first, second) = client.events
        assertEquals(first.distinctId, second.distinctId)
        assertEquals(first.distinctId, first.properties["\$session_id"])
        assertEquals(false, first.properties["\$process_person_profile"])
        assertEquals("Network", second.properties["screen"])
    }

    @Test
    fun `full mode persists a stable installation id`() {
        val preferences = FakePreferences(level = TelemetryLevel.Full)
        val client = FakeClient()
        val manager = manager(preferences, client)

        manager.track(TelemetryEvents.APP_STARTED)

        val id = assertNotNull(preferences.installationId)
        assertEquals(id, client.events.single().distinctId)
    }

    @Test
    fun `leaving full mode deletes the installation id`() {
        val preferences = FakePreferences(level = TelemetryLevel.Full, installationId = "stored-id")
        val manager = manager(preferences, FakeClient())

        manager.setLevel(TelemetryLevel.Anonymous)

        assertNull(preferences.installationId)
        assertFalse(manager.state.value.shouldAskConsent)
    }

    @Test
    fun `disabled sends nothing`() {
        val client = FakeClient()
        val manager = manager(FakePreferences(), client)

        manager.setLevel(TelemetryLevel.Disabled)
        manager.track(TelemetryEvents.APP_STARTED)

        assertTrue(client.events.isEmpty())
    }

    @Test
    fun `do not track parsing`() {
        assertFalse(TelemetryConfig.isDoNotTrack(null))
        assertFalse(TelemetryConfig.isDoNotTrack(""))
        assertFalse(TelemetryConfig.isDoNotTrack("0"))
        assertFalse(TelemetryConfig.isDoNotTrack("false"))
        assertTrue(TelemetryConfig.isDoNotTrack("1"))
        assertTrue(TelemetryConfig.isDoNotTrack("true"))
    }
}
