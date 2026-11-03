package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconDashboardPluginTest {

    private class CountingSender : FloconMessageSender {
        val sent = AtomicInteger()
        override fun send(plugin: String, method: String, body: String) {
            sent.incrementAndGet()
        }
        override fun sendPendingMessages() = Unit
    }

    private fun dashboard(index: Int) = DashboardConfig(id = "d$index", containers = emptyList())

    @Test
    fun `connecting while dashboards are being registered does not crash`() = runBlocking(Dispatchers.Default) {
        repeat(200) {
            val plugin = FloconDashboardPluginImpl(CountingSender(), FloconEncoder())
            repeat(300) { plugin.registerDashboard(dashboard(it)) }
            // registrations are still pending on the plugin dispatcher: must not throw
            repeat(5) { plugin.onConnectedToServer() }
        }
    }

    @Test
    fun `sends every registered dashboard again on connect`() = runBlocking {
        val sender = CountingSender()
        val plugin = FloconDashboardPluginImpl(sender, FloconEncoder())
        repeat(3) { plugin.registerDashboard(dashboard(it)) }
        waitFor { sender.sent.get() == 3 }
        plugin.onConnectedToServer()
        waitFor { sender.sent.get() == 6 }
        assertEquals(6, sender.sent.get())
    }

    private fun waitFor(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 5_000
        while (!condition() && System.currentTimeMillis() < deadline) Thread.sleep(5)
    }
}
