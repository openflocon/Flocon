package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.DeviceDataBaseDataModel
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.QueryResultDataModel
import io.github.openflocon.flocon.database.core.model.todevice.DatabaseQueryMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloconDatabasePluginTest {

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

    private class FakeFloconDatabaseProvider : FloconDatabaseProvider {
        override fun getAllDataBases(prefix: List<String>) = listOf(
            FloconDatabaseModel(id = "test_db", displayName = "Test Database")
        )
        override fun executeQuery(query: String): DatabaseExecuteSqlResponse.Success = DatabaseExecuteSqlResponse.Success(
            result = "result",
            affectedRows = 1
        )
        override fun register(model: FloconDatabaseModel) {}
    }

    @Test
    fun `onConnectedToServer sends all databases`() = runTest {
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val scope = TestScope(UnconfinedTestDispatcher())
        val providers = listOf(FakeFloconDatabaseProvider())
        val plugin = FloconDatabasePluginImpl(sender, scope, providers, encoder)

        plugin.onConnectedToServer()

        assertEquals("Database", sender.lastSentBody?.contains("test_db") ?: false)
        assertTrue(sender.lastSentBody!!.contains("Test Database"))
    }

    @Test
    fun `sendAllDatabases deduplicates databases by id`() = runTest {
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val scope = TestScope(UnconfinedTestDispatcher())
        
        val provider1 = object : FloconDatabaseProvider {
            override fun getAllDataBases(prefix: List<String>) = listOf(
                FloconDatabaseModel(id = "db1", displayName = "Database 1")
            )
            override fun executeQuery(query: String) = DatabaseExecuteSqlResponse.Success("result", 1)
            override fun register(model: FloconDatabaseModel) {}
        }
        
        val provider2 = object : FloconDatabaseProvider {
            // Returns same ID but different name (should overwrite)
            override fun getAllDataBases(prefix: List<String>) = listOf(
                FloconDatabaseModel(id = "db1", displayName = "Database 1 - Duplicate")
            )
            override fun executeQuery(query: String) = DatabaseExecuteSqlResponse.Success("result", 1)
            override fun register(model: FloconDatabaseModel) {}
        }

        val providers = listOf(provider1, provider2)
        val plugin = FloconDatabasePluginImpl(sender, scope, providers, encoder)

        plugin.onConnectedToServer()

        // Should only have one entry for db1
        // Since we overwrite in map, it should be the last one in the list of providers
        assertTrue(sender.lastSentBody!!.contains("Database 1 - Duplicate"))
        assertTrue(!sender.lastSentBody!!.contains("Database 1"))
    }

    @Test
    fun `register deduplicates databases by id`() = runTest {
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val scope = TestScope(UnconfinedTestDispatcher())
        val providers = listOf(FakeFloconDatabaseProvider())
        val plugin = FloconDatabasePluginImpl(sender, scope, providers, encoder)

        // Register two with same ID
        val model1 = FloconDatabaseModel(id = "db1", displayName = "Name 1")
        val model2 = FloconDatabaseModel(id = "db1", displayName = "Name 2")

        plugin.register(model1)
        plugin.register(model2)

        // onConnectedToServer should send the one from register (the latest one)
        plugin.onConnectedToServer()

        assertTrue(sender.lastSentBody!!.contains("Name 2"))
        assertTrue(!sender.lastSentBody!!.contains("Name 1"))
    }
}
