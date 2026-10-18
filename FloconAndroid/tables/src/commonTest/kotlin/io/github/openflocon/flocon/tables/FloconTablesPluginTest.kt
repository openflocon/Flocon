package io.github.openflocon.flocon.tables

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.tables.model.TableItem
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloconTablesPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        var lastSentBody: String? = null
        override fun send(plugin: String, method: String, body: String) {
            lastSentBody = body
        }
        override fun sendPendingMessages() {}
    }

    private class FakeFloconEncoder : FloconEncoder {
        override fun <T> encode(serializer: KSerializer<T>, body: T): String = "encoded_${body.toString()}"
        override fun <T> decode(serializer: KSerializer<T>, body: String): T = throw UnsupportedOperationException()
    }

    @Test
    fun `registerItems sends correct message`() = runTest {
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val plugin = FloconTablePluginImpl(sender, encoder)
        
        val tableItems = listOf(
            TableItem(id = "1", name = "item1", data = "data1")
        )

        plugin.registerItems(tableItems)

        assertEquals("Table", sender.lastSentBody?.contains("Table") ?: false)
        assertEquals("AddItems", sender.lastSentBody?.contains("AddItems") ?: false)
        assertTrue(sender.lastSentBody!!.contains("item1"))
    }
}
