package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconDevicePluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconDevicePluginImpl(
        sender = sender,
        context = FloconContext(appName = "app", packageName = "app.package"),
        encoder = FloconEncoder(),
    )

    @Test
    fun `registerWithSerial sends the serial`() {
        plugin.registerWithSerial("emulator-5554")

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Device.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Device.Method.RegisterDevice, method)
        assertEquals("{\"serial\":\"emulator-5554\"}", body)
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Device.Plugin, plugin.key)
    }
}
