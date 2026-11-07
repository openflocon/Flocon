package com.flocon.data.remote.mock

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class MockServerTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val launch = Instant.fromEpochMilliseconds(1_800_000_000_000)

    private val scenario = scenario("s") {
        android("Pixel 8", app = "App", packageName = "a.b") {
            network(2.minutes.ago, "GET", "https://x")
            timeline(1.minutes.ago, "hello")
        }
        ios("iPhone 16", app = "App", packageName = "c.d", appStartedAt = 10.minutes.ago) {
            timeline(3.minutes.ago, "ios")
        }
    }

    private fun server() = MockServer(scenario, json, defaultSdkVersion = "9.9.9", launchTime = launch)

    private val expected = MockMessageEncoder(json).encode(scenario, launch, "9.9.9")

    @Test
    fun `exposes scenario devices as active`() = runBlocking {
        val devices = server().activeDevices.first()

        assertEquals(
            setOf(
                FloconDeviceIdAndPackageNameDataModel(deviceId = "pixel-8", packageName = "a.b", appInstance = (launch - 2.minutes - 1.seconds).toEpochMilliseconds()),
                FloconDeviceIdAndPackageNameDataModel(deviceId = "iphone-16", packageName = "c.d", appInstance = (launch - 10.minutes).toEpochMilliseconds()),
            ),
            devices,
        )
    }

    @Test
    fun `emits all scenario messages`() = runBlocking {
        val messages = server().receivedMessages.take(expected.size).toList()

        assertEquals(expected, messages)
    }

    @Test
    fun `emits scenario only once across restarts`() = runBlocking {
        val server = server()
        server.receivedMessages.take(expected.size).toList()

        server.stop()
        server.startWebsocket(9023)
        server.starHttp(9024)

        assertNull(withTimeoutOrNull(200.milliseconds) { server.receivedMessages.first() })
    }

    @Test
    fun `sendMessageToClient does not throw`() = runBlocking {
        server().sendMessageToClient(
            deviceIdAndPackageName = FloconDeviceIdAndPackageNameDataModel(deviceId = "pixel-8", packageName = "a.b", appInstance = 1L),
            message = FloconOutgoingMessageDataModel(plugin = "featureflags", method = "set", body = "{}"),
        )
    }
}
