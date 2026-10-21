package io.github.openflocon.flocon.featureflags

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlag
import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlagState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(FloconMarker::class)
class FloconFeatureFlagsPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconFeatureFlagsPluginImpl(sender, FloconEncoder(), Dispatchers.Unconfined)

    private val darkMode = FloconFeatureFlag("dark_mode", FloconFeatureFlagState.On, "on")
    private val checkout = FloconFeatureFlag("new_checkout", FloconFeatureFlagState.Custom, "treatment", """{"steps":3}""")

    @Test
    fun `register sends the flags of the provider`() {
        plugin.register("firebase", darkMode, checkout)

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.FeatureFlags.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.FeatureFlags.Method.Register, method)
        assertEquals(
            "{\"provider\":\"firebase\",\"flags\":[" +
                "{\"name\":\"dark_mode\",\"state\":\"on\",\"variant\":\"on\"}," +
                "{\"name\":\"new_checkout\",\"state\":\"custom\",\"variant\":\"treatment\",\"payload\":\"{\\\"steps\\\":3}\"}]}",
            body,
        )
    }

    @Test
    fun `the known flags are sent again when the app reconnects`() = runTest {
        plugin.register("firebase", listOf(darkMode))
        plugin.register("statsig", listOf(checkout))
        sender.messages.clear()

        plugin.onConnectedToServer()

        assertEquals(2, sender.messages.size)
    }

    @Test
    fun `key is the plugin name sent to the desktop`() {
        assertEquals(Protocol.FromDevice.FeatureFlags.Plugin, plugin.key)
    }
}
