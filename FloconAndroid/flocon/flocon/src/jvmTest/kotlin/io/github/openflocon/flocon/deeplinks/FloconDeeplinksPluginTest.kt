package io.github.openflocon.flocon.deeplinks

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class FloconDeeplinksPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    @Test
    fun `configured deeplinks are sent when connected`() = runTest {
        val sender = FakeFloconMessageSender()
        val config = FloconDeeplinksConfigImpl().apply {
            variable("userId")
            deeplink("flocon://user/[userId]") {
                label = "User"
                "userId" withAutoComplete listOf("Florent", "David")
            }
        }
        val plugin = FloconDeeplinksPluginImpl(
            deeplinks = config.deeplinks(),
            variables = config.variables(),
            sender = sender,
            encoder = FloconEncoder(FloconDeeplinkEncoding().serializersModule),
        )

        plugin.onConnectedToServer()

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Deeplink.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Deeplink.Method.GetDeeplinks, method)
        assertContains(body, "flocon://user/[userId]")
        assertContains(body, "Florent")
    }

    @Test
    fun `deeplinks registered after install are sent and kept for reconnection`() = runTest {
        val sender = FakeFloconMessageSender()
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val config = FloconDeeplinksConfigImpl().apply { deeplink("flocon://home") }
        val plugin = FloconDeeplinksPluginImpl(
            deeplinks = config.deeplinks(),
            variables = config.variables(),
            sender = sender,
            encoder = FloconEncoder(FloconDeeplinkEncoding().serializersModule),
            dispatcher = dispatcher,
        )

        plugin.register {
            variable("userId")
            deeplink("flocon://user/[userId]") { "userId" withAutoComplete listOf("Florent") }
        }
        assertEquals(1, sender.messages.size)
        assertContains(sender.messages.last().third, "flocon://home")
        assertContains(sender.messages.last().third, "flocon://user/[userId]")

        plugin.onConnectedToServer()
        assertEquals(2, sender.messages.size)
        assertContains(sender.messages.last().third, "flocon://user/[userId]")
    }
}
