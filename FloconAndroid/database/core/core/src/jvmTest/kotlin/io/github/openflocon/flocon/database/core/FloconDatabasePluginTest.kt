package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.database.core.model.FloconFileDatabaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import io.github.openflocon.flocon.utils.currentTimeMillis
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertTrue
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
    fun `logQuery timestamp is the time of the call, not the time the message is sent`() {
        // a dispatcher that holds the launched coroutine until we run it, like a busy dispatcher
        val queued = mutableListOf<Runnable>()
        val delayedDispatcher = object : CoroutineDispatcher() {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                queued += block
            }
        }
        val delayedSender = FakeFloconMessageSender()
        val delayedPlugin = FloconDatabasePluginImpl(
            sender = delayedSender,
            scope = CoroutineScope(delayedDispatcher),
            providers = emptyList(),
            encoder = FloconEncoder(FloconDatabaseEncoding().serializersModule),
        )

        val before = currentTimeMillis()
        delayedPlugin.logQuery(dbId = "dogs", sqlQuery = "SELECT 1", bindArgs = emptyList())
        val afterCall = currentTimeMillis()
        Thread.sleep(50)
        queued.toList().forEach { it.run() }

        val body = delayedSender.messages.single().third
        val timestamp = Regex("\"timestamp\":(\\d+)").find(body)!!.groupValues[1].toLong()
        assertTrue(timestamp in before..afterCall, "timestamp $timestamp is not in $before..$afterCall")
    }

    @Test
    fun `logQuery sends the transaction id`() {
        plugin.logQuery(
            dbId = "dogs",
            sqlQuery = "SAVEPOINT s1",
            bindArgs = emptyList(),
            transaction = FloconTransactionInfo(transactionId = "tx-1", nestedTransactionId = "n-1", parentTransactionId = "tx-1", depth = 1),
        )

        val body = sender.messages.single().third
        assertContains(body, "\"transactionId\":\"tx-1\"")
        assertContains(body, "\"nestedTransactionId\":\"n-1\"")
        assertContains(body, "\"parentTransactionId\":\"tx-1\"")
        assertContains(body, "\"transactionDepth\":1")
    }

    @Test
    fun `logQuery groups the queries of a transaction without a given id`() {
        plugin.logQuery("dogs", "BEGIN", emptyList())
        plugin.logQuery("dogs", "INSERT INTO dog VALUES (1)", emptyList())
        plugin.logQuery("dogs", "COMMIT", emptyList())
        plugin.logQuery("dogs", "SELECT 1", emptyList())
        plugin.logQuery("cats", "BEGIN", emptyList())

        val ids = sender.messages.map { Regex("\"transactionId\":\"([^\"]+)\"").find(it.third)?.groupValues?.get(1) }
        assertEquals(ids[0], ids[1])
        assertEquals(ids[0], ids[2])
        assertEquals(null, ids[3])
        assertEquals(true, ids[4] != null && ids[4] != ids[0])
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
