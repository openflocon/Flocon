package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.database.core.model.FloconFileDatabaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
        plugin.logQuery(dbId = "dogs", sqlQuery = "SELECT * FROM dog WHERE id = ?", bindArgs = listOf(1))

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Database.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Database.Method.LogQuery, method)
        assertContains(body, "\"dbId\":\"dogs\"")
        assertContains(body, "\"bindArgs\":[\"1\"]")
    }

    @Test
    fun `register refuses a second database with the same id`() {
        plugin.register(FloconFileDatabaseModel(id = "dogs", displayName = "dogs", absolutePath = "/a"))

        assertFailsWith<IllegalArgumentException> {
            plugin.register(FloconFileDatabaseModel(id = "dogs", displayName = "other", absolutePath = "/b"))
        }
    }

    @Test
    fun `register sends the databases to the desktop with their id and name`() {
        plugin.register(FloconFileDatabaseModel(id = "dogs_room3", displayName = "dogs", absolutePath = "/a"))

        val (_, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Database.Method.GetDatabases, method)
        assertContains(body, "\"id\":\"dogs_room3\"")
        assertContains(body, "\"name\":\"dogs\"")
    }

    @Test
    fun `register accepts the same database twice`() {
        val database = FloconFileDatabaseModel(id = "dogs", displayName = "dogs", absolutePath = "/a")

        plugin.register(database)
        plugin.register(database)
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Database.Plugin, plugin.key)
    }
}
