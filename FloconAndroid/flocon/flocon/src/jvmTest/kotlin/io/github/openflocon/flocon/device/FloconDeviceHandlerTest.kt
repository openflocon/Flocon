package io.github.openflocon.flocon.device

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FloconDeviceHandlerTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val handler = FloconDeviceHandler(
        sender = sender,
        context = FloconContext(appName = "app", packageName = "app.package"),
        encoder = FloconEncoder(),
    )

    @Test
    fun `get_app_icon is handled by the core`() {
        // on the JVM there is no icon, so nothing is sent
        assertEquals(
            true,
            handler.handle(Protocol.ToDevice.Device.Plugin, Protocol.ToDevice.Device.Method.GetAppIcon),
        )
        assertEquals(emptyList(), sender.messages)
    }

    @Test
    fun `restart_app is left to the device plugin`() {
        assertFalse(handler.handle(Protocol.ToDevice.Device.Plugin, Protocol.ToDevice.Device.Method.RestartApp))
    }

    @Test
    fun `other plugins are ignored`() {
        assertFalse(handler.handle("network", Protocol.ToDevice.Device.Method.GetAppIcon))
    }
}
