package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.Protocol
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconDevicePluginTest {

    private val plugin = FloconDevicePluginImpl(
        context = FloconContext(appName = "app", packageName = "app.package"),
    )

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Device.Plugin, plugin.key)
    }
}
