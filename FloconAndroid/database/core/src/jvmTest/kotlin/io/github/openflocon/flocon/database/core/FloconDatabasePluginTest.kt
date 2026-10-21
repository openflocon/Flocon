package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class FloconDatabasePluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconDatabasePluginImpl(
        sender = sender,
        scope = CoroutineScope(Dispatchers.Unconfined),
        providers = emptyList(),
        encoder = FloconEncoder(FloconDatabaseEncoding().serializersModule),
    )

    @Test
    fun `logQuery sends the query`() {
        plugin.logQuery(dbName = "dogs", sqlQuery = "SELECT * FROM dog WHERE id = ?", bindArgs = listOf(1))

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Database.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Database.Method.LogQuery, method)
        assertContains(body, "\"dbName\":\"dogs\"")
        assertContains(body, "\"bindArgs\":[\"1\"]")
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Database.Plugin, plugin.key)
    }
}
