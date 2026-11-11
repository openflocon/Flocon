package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class AppStartedTelemetryTest {

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
        val client = FakeTelemetryRepository()

        telemetry(telemetryManager(client)).send()

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
        val client = FakeTelemetryRepository()

        telemetry(
            telemetryManager(client),
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
    fun `waits for the opt-in instead of being lost`() = runBlocking {
        val client = FakeTelemetryRepository(level = null)
        val manager = telemetryManager(client)

        val job = launch(Dispatchers.Default) { telemetry(manager).send() }
        delay(200.milliseconds)
        assertTrue(client.events.isEmpty())

        manager.setLevel(TelemetryLevelDomainModel.Anonymous)
        job.join()

        assertEquals(TelemetryEvents.APP_STARTED, client.events.single().first)
    }

    @Test
    fun `is sent once per launch`() = runBlocking {
        val client = FakeTelemetryRepository()
        val telemetry = telemetry(telemetryManager(client))

        telemetry.send()
        telemetry.send()

        assertEquals(1, client.events.size)
    }
}
