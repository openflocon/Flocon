package com.flocon.data.remote.mock

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MockScenarioDslTest {

    @Test
    fun `builds android device with defaults`() {
        val scenario = scenario("s") {
            android("Pixel 8", app = "App", packageName = "a.b") {}
        }

        assertEquals("s", scenario.name)
        val device = scenario.devices.single()
        assertEquals("android", device.platform)
        assertEquals("Pixel 8", device.name)
        assertEquals("App", device.appName)
        assertEquals("a.b", device.packageName)
        assertNull(device.appStartedAt)
        assertNull(device.sdkVersion)
        assertTrue(device.events.isEmpty())
    }

    @Test
    fun `ios device`() {
        val scenario = scenario("s") {
            ios("iPhone 16", app = "App", packageName = "a.b") {}
        }

        assertEquals("ios", scenario.devices.single().platform)
    }

    @Test
    fun `network call defaults`() {
        val scenario = scenario("s") {
            android("Pixel 8", app = "App", packageName = "a.b") {
                network(5.minutes.ago, "GET", "https://x")
            }
        }

        val call = assertIs<MockEvent.NetworkCall>(scenario.devices.single().events.single())
        assertEquals(5.minutes, call.at)
        assertEquals("GET", call.method)
        assertEquals("https://x", call.url)
        assertEquals(200, call.status)
        assertEquals(100.milliseconds, call.duration)
        assertFalse(call.pending)
        assertNull(call.error)
    }

    @Test
    fun `network block sets headers and bodies`() {
        val scenario = scenario("s") {
            android("Pixel 8", app = "App", packageName = "a.b") {
                network(1.minutes.ago, "POST", "https://x") {
                    requestHeader("authorization", "Bearer x")
                    requestBody("""{"a":1}""")
                    responseHeader("content-type", "application/json")
                    responseBody("[]")
                    contentType("application/json")
                }
            }
        }

        val call = assertIs<MockEvent.NetworkCall>(scenario.devices.single().events.single())
        assertEquals(mapOf("authorization" to "Bearer x"), call.requestHeaders)
        assertEquals("""{"a":1}""", call.requestBody)
        assertEquals(mapOf("content-type" to "application/json"), call.responseHeaders)
        assertEquals("[]", call.responseBody)
        assertEquals("application/json", call.contentType)
    }

    @Test
    fun `repeat generates events`() {
        val scenario = scenario("s") {
            android("Pixel 8", app = "App", packageName = "a.b") {
                repeat(200) { network(it.seconds.ago, "GET", "u/$it") }
            }
        }

        assertEquals(200, scenario.devices.single().events.size)
    }

    @Test
    fun `other plugins are added as events`() {
        val scenario = scenario("s") {
            android("Pixel 8", app = "App", packageName = "a.b") {
                timeline(6.minutes.ago, "App started", type = "lifecycle", level = null)
                crash(2.minutes.ago, exceptionType = "java.lang.IllegalStateException", message = "boom")
                analytics(1.minutes.ago, "screen_view", "screen" to "home")
                featureFlags("firebase") {
                    flag("new_checkout", state = "on", variant = "on")
                }
                raw(30.seconds.ago, plugin = "network", method = "log_network_call_request", body = "{not json")
            }
        }

        val events = scenario.devices.single().events
        val timeline = assertIs<MockEvent.TimelineLog>(events[0])
        assertEquals("lifecycle", timeline.type)
        assertNull(timeline.level)
        assertIs<MockEvent.Crash>(events[1])
        val analytics = assertIs<MockEvent.AnalyticsEvent>(events[2])
        assertEquals(mapOf("screen" to "home"), analytics.properties)
        val flags = assertIs<MockEvent.FeatureFlags>(events[3])
        assertEquals("firebase", flags.provider)
        assertEquals("new_checkout", flags.flags.single().name)
        val raw = assertIs<MockEvent.Raw>(events[4])
        assertEquals("{not json", raw.body)
    }
}
