package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.dashboard.dsl.text
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import kotlinx.coroutines.Dispatchers
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

    private fun emptyDashboard(index: Int) = DashboardConfig(id = "d$index", containers = emptyList())

    @Test
    fun `connecting while dashboards are being registered does not crash`() = runBlocking(Dispatchers.Default) {
        repeat(200) {
            val plugin = FloconDashboardPluginImpl(FakeFloconMessageSender(), FloconEncoder())
            repeat(300) { plugin.registerDashboard(emptyDashboard(it)) }
            // registrations are still pending on the plugin dispatcher: must not throw
            repeat(5) { plugin.onConnectedToServer() }
        }
    }

    @Test
    fun `sends every registered dashboard again on connect`() = runBlocking {
        val sender = FakeFloconMessageSender()
        val plugin = FloconDashboardPluginImpl(sender, FloconEncoder())
        repeat(3) { plugin.registerDashboard(emptyDashboard(it)) }
        withTimeout(5_000) { while (sender.messages.size < 3) delay(10) }
        plugin.onConnectedToServer()
        withTimeout(5_000) { while (sender.messages.size < 6) delay(10) }
        assertEquals(6, sender.messages.size)
    }
}
