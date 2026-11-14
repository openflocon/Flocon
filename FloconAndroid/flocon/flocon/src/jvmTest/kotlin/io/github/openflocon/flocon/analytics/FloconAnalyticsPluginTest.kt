package io.github.openflocon.flocon.analytics

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.analytics.model.analyticsProperty
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class FloconAnalyticsPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconAnalyticsPluginImpl(sender, FloconEncoder())

    @Test
    fun `log sends the event and its properties`() {
        plugin.log(AnalyticsEvent("click", "events", "screen" analyticsProperty "home"))

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Analytics.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Analytics.Method.AddItems, method)
        assertContains(body, "\"event_name\":\"click\"")
        assertContains(body, "\"analytics_table_id\":\"events\"")
        assertContains(body, "\"name\":\"screen\"")
    }

    @Test
    fun `sender reuses its table id`() {
        val firebase = plugin.sender("firebase")

        firebase.log("click", "screen" analyticsProperty "home")
        firebase.log(AnalyticsEvent("view", "ignored"))

        assertEquals(2, sender.messages.size)
        sender.messages.forEach { assertContains(it.third, "\"analytics_table_id\":\"firebase\"") }
    }

    @Test
    fun `log sends nothing for an empty list`() {
        plugin.log(emptyList())

        assertEquals(0, sender.messages.size)
    }
}
