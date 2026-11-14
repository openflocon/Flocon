package io.github.openflocon.flocon.timeline

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.timeline.model.FloconTimelineLevel
import io.github.openflocon.flocon.timeline.model.FloconTimelineType
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloconTimelinePluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconTimelinePluginImpl(sender, FloconEncoder(), Dispatchers.Unconfined)

    private fun sentJson(): JsonObject {
        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Timeline.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Timeline.Method.Log, method)
        return Json.parseToJsonElement(body).jsonObject
    }

    private fun JsonObject.assertTimestampPresent() {
        assertTrue(getValue("timestamp").jsonPrimitive.content.toLong() > 0)
    }

    @Test
    fun `logNavigation sends the reserved type and no level`() {
        plugin.logNavigation("home", mapOf("from" to "splash"))

        val json = sentJson()
        assertEquals("navigation", json.getValue("type").jsonPrimitive.content)
        assertEquals("home", json.getValue("value").jsonPrimitive.content)
        assertEquals("splash", json.getValue("params").jsonObject.getValue("from").jsonPrimitive.content)
        assertFalse(json.containsKey("level"))
        json.assertTimestampPresent()
    }

    @Test
    fun `logUserAction sends the reserved type and no level`() {
        plugin.logUserAction("tap buy")

        val json = sentJson()
        assertEquals("userAction", json.getValue("type").jsonPrimitive.content)
        assertEquals("tap buy", json.getValue("value").jsonPrimitive.content)
        assertFalse(json.containsKey("level"))
    }

    @Test
    fun `logLifecycle sends the reserved type and no level`() {
        plugin.logLifecycle("onResume")

        val json = sentJson()
        assertEquals("lifecycle", json.getValue("type").jsonPrimitive.content)
        assertEquals("onResume", json.getValue("value").jsonPrimitive.content)
        assertFalse(json.containsKey("level"))
    }

    @Test
    fun `log typed sends the type name and no level`() {
        plugin.log(FloconTimelineType("billing"), "purchase started", mapOf("sku" to "pro"))

        val json = sentJson()
        assertEquals("billing", json.getValue("type").jsonPrimitive.content)
        assertEquals("purchase started", json.getValue("value").jsonPrimitive.content)
        assertEquals("pro", json.getValue("params").jsonObject.getValue("sku").jsonPrimitive.content)
        assertFalse(json.containsKey("level"))
    }

    @Test
    fun `log tag sends the tag as type and the level`() {
        plugin.log("payments", "declined", level = FloconTimelineLevel.Warning)

        val json = sentJson()
        assertEquals("payments", json.getValue("type").jsonPrimitive.content)
        assertEquals("declined", json.getValue("value").jsonPrimitive.content)
        assertEquals("warning", json.getValue("level").jsonPrimitive.content)
        json.assertTimestampPresent()
    }

    @Test
    fun `log tag default level is info`() {
        plugin.log("payments", "started")

        assertEquals("info", sentJson().getValue("level").jsonPrimitive.content)
    }

    @Test
    fun `log params are omitted or empty by default`() {
        plugin.logNavigation("home")

        // the encoder does not encode default values: the desktop defaults params to empty
        assertEquals(0, sentJson()["params"]?.jsonObject?.size ?: 0)
    }

    @Test
    fun `key is the plugin name sent to the desktop`() {
        assertEquals(Protocol.FromDevice.Timeline.Plugin, plugin.key)
    }
}
