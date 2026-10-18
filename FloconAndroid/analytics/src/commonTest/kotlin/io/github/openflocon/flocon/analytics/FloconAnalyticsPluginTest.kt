package io.github.openflocon.flocon.analytics

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.analytics.model.toItem
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloconAnalyticsPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        var lastSentBody: String? = null
        var lastSentPlugin: String? = null
        var lastSentMethod: String? = null

        override fun send(plugin: String, method: String, body: String) {
            lastSentPlugin = plugin
            lastSentMethod = method
            lastSentBody = body
        }

        override fun sendPendingMessages() {}
    }

    private class FakeFloconEncoder : FloconEncoder {
        override fun <T> encode(serializer: KSerializer<T>, body: T): String {
            return "encoded_${body.toString()}"
        }

        override fun <T> decode(serializer: KSerializer<T>, body: String): T {
            throw UnsupportedOperationException()
        }
    }

    @Test
    fun `log analytics item sends correct message`() = runTest {
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val plugin = FloconAnalyticsPluginImpl(sender, encoder)
        
        val event = AnalyticsEvent(
            name = "test_event",
            properties = mapOf("key" to "value")
        )

        plugin.log(event)

        assertEquals("Analytics", sender.lastSentPlugin)
        assertEquals("AddItems", sender.lastSentMethod)
        assertTrue(sender.lastSentBody!!.contains("test_event"))
    }

    @Test
    fun `log multiple analytics items sends correct message`() = runTest {
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val plugin = FloconAnalyticsPluginImpl(sender, encoder)
        
        val events = listOf(
            AnalyticsEvent(name = "event1", properties = mapOf()),
            AnalyticsEvent(name = "event2", properties = mapOf())
        )

        plugin.log(events)

        assertEquals("Analytics", sender.lastSentPlugin)
        assertEquals("AddItems", sender.lastSentMethod)
        assertTrue(sender.lastSentBody!!.contains("event1"))
        assertTrue(sender.lastSentBody!!.contains("event2"))
    }
}
