package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.PlainTextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.ContainerType
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloconDashboardPluginTest {

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

    @Test
    fun `registerDashboard sends correct message`() = runTest {
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val context = FloconContext()
        val plugin = FloconDashboardPluginImpl(sender, encoder)
        
        val section = SectionConfig(
            name = "Test Section",
            elements = listOf(
                PlainTextConfig(label = "Label", value = "Value", type = "text")
            )
        )

        val dashboardConfig = DashboardConfig(
            id = "test_dashboard",
            containers = listOf(section)
        )

        plugin.registerDashboard(dashboardConfig)

        assertEquals("Dashboard", sender.lastSentBody?.contains("Dashboard") ?: false)
        assertEquals("AddItems", sender.lastSentBody?.contains("AddItems") ?: false)
        assertTrue(sender.lastSentBody!!.contains("Test Section"))
    }
}
