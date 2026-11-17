package io.github.openflocon.flocon.tables

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.tables.dsl.table
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class FloconTablesPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconTablePluginImpl(sender, FloconEncoder())

    @Test
    fun `table sends the table with its columns`() {
        plugin.table("logs") {
            column("level", "info")
            column("message", "hello")
        }

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Table.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Table.Method.AddItems, method)
        assertContains(body, "\"name\":\"logs\"")
        assertContains(body, "{\"column\":\"level\",\"value\":\"info\"}")
        assertContains(body, "{\"column\":\"message\",\"value\":\"hello\"}")
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Table.Plugin, plugin.key)
    }
}
