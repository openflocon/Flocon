package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.FloconContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class FloconDevicePluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        var lastSentBody: String? = null
        override fun send(plugin: String, method: String, body: String) {
            lastSentBody = body
        }
        override fun sendPendingMessages() {}
    }

    @Test
    fun `registerWithSerial does not throw`() = runTest {
        val sender = FakeFloconMessageSender()
        val context = FloconContext()
        val plugin = FloconDevicePluginImpl(sender, context)

        plugin.registerWithSerial("test_serial")
        assertTrue(true)
    }
}
