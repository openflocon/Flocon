package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.dashboard.dsl.text
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class FloconDashboardPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = CopyOnWriteArrayList<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconDashboardPluginImpl(sender, FloconEncoder())

    @Test
    fun `dashboard registers the dashboard`() = runBlocking {
        val job = plugin.dashboard(id = "main", scope = this) {
            section(name = "user") {
                text(label = "name", value = "Florent")
            }
        }

        val (pluginName, method, body) = withTimeout(5_000) {
            while (sender.messages.isEmpty()) delay(10)
            sender.messages.first()
        }
        job.cancel()

        assertEquals(Protocol.FromDevice.Dashboard.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Dashboard.Method.Update, method)
        assertContains(body, "Florent")
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Dashboard.Plugin, plugin.key)
    }
}
