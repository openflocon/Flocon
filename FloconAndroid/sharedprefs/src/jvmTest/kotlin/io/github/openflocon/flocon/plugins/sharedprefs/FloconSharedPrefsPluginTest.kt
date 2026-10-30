package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceType
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FloconSharedPrefsPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private class InMemoryPreference(
        override val name: String,
        override val type: String = FloconPreferenceType.Custom,
    ) : FloconPreference {
        val values = mutableMapOf(
            "age" to FloconPreferenceValue(intValue = 34),
            "token" to FloconPreferenceValue(stringValue = "abc"),
        )
        override suspend fun set(columnName: String, value: FloconPreferenceValue) {
            values[columnName] = value
        }
        override suspend fun remove(columnName: String) {
            values -= columnName
        }
        override suspend fun columns(): List<String> = values.keys.toList()
        override suspend fun get(columnName: String): FloconPreferenceValue? = values[columnName]
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconSharedPrefsPluginImpl(
        context = FloconContext(appName = "app", packageName = "app.package"),
        sender = sender,
        encoder = FloconEncoder(),
    )
    private val preference = InMemoryPreference("user")

    @Test
    fun `register sends the preferences list`() {
        plugin.register(preference)

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.SharedPreferences.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences, method)
        assertEquals("[{\"name\":\"user\",\"type\":\"Custom\"}]", body)
    }

    @Test
    fun `register sends the type of each preference`() {
        plugin.register(InMemoryPreference(name = "settings", type = FloconPreferenceType.DataStore))
        plugin.register(InMemoryPreference(name = "room", type = "Room"))

        val (_, _, body) = sender.messages.last()
        assertEquals(
            "[{\"name\":\"room\",\"type\":\"Room\"},{\"name\":\"settings\",\"type\":\"DataStore\"}]",
            body,
        )
    }

    @Test
    fun `getSharedPreferenceValue sends the values`() = runTest {
        plugin.register(preference)

        plugin.onMessageReceived(
            method = Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferenceValue,
            body = "{\"requestId\":\"1\",\"sharedPreferenceName\":\"user\"}",
        )

        val (_, method, body) = sender.messages.last()
        assertEquals(Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue, method)
        assertContains(body, "{\"key\":\"age\",\"intValue\":34}")
    }

    @Test
    fun `setSharedPreferenceValue edits the preference`() = runTest {
        plugin.register(preference)

        plugin.onMessageReceived(
            method = Protocol.ToDevice.SharedPreferences.Method.SetSharedPreferenceValue,
            body = "{\"requestId\":\"1\",\"sharedPreferenceName\":\"user\",\"key\":\"age\",\"intValue\":35}",
        )

        assertEquals(35, preference.values["age"]?.intValue)
        assertContains(sender.messages.last().third, "{\"key\":\"age\",\"intValue\":35}")
    }

    @Test
    fun `removeSharedPreferenceValue removes the key and sends the values`() = runTest {
        plugin.register(preference)

        plugin.onMessageReceived(
            method = Protocol.ToDevice.SharedPreferences.Method.RemoveSharedPreferenceValue,
            body = "{\"requestId\":\"1\",\"sharedPreferenceName\":\"user\",\"key\":\"token\"}",
        )

        assertFalse("token" in preference.values)
        val (_, method, body) = sender.messages.last()
        assertEquals(Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue, method)
        assertContains(body, "{\"key\":\"age\",\"intValue\":34}")
        assertFalse(body.contains("token"))
    }

    @Test
    fun `removeSharedPreferenceValue keeps the key when remove is not implemented`() = runTest {
        val readOnly = object : FloconPreference {
            override val name: String = "readOnly"
            override suspend fun set(columnName: String, value: FloconPreferenceValue) = Unit
            override suspend fun columns(): List<String> = listOf("age")
            override suspend fun get(columnName: String) = FloconPreferenceValue(intValue = 34)
        }
        plugin.register(readOnly)

        plugin.onMessageReceived(
            method = Protocol.ToDevice.SharedPreferences.Method.RemoveSharedPreferenceValue,
            body = "{\"requestId\":\"1\",\"sharedPreferenceName\":\"readOnly\",\"key\":\"age\"}",
        )

        assertContains(sender.messages.last().third, "{\"key\":\"age\",\"intValue\":34}")
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.SharedPreferences.Plugin, plugin.key)
    }
}
