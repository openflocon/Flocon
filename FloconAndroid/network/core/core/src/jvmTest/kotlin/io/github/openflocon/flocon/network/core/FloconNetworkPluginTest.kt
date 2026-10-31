package io.github.openflocon.flocon.network.core

import io.github.openflocon.flocon.Protocol
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconNetworkPluginTest {

    @Test
    fun `plugin id is the plugin name sent by the desktop`() {
        // messages from the desktop (mocks, bad network config) are routed with this name
        assertEquals(Protocol.ToDevice.Network.Plugin, FloconNetwork.pluginId)
    }
}
