package io.github.openflocon.flocon.network.core.noop.plugin

import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.network.core.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.network.core.model.FloconWebSocketEvent
import io.github.openflocon.flocon.network.core.model.FloconWebSocketMockListener
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FloconNetworkPluginNoOpTest {

    @Test
    fun `mocks are empty`() {
        val plugin = FloconNetworkPluginImpl()
        assertEquals(0, plugin.mocks.size)
    }

    @Test
    fun `badQualityConfig is null`() {
        val plugin = FloconNetworkPluginImpl()
        assertNull(plugin.badQualityConfig)
    }

    @Test
    fun `onMessageReceived does nothing`() = runTest {
        val plugin = FloconNetworkPluginImpl()
        plugin.onMessageReceived("method", "body")
    }

    @Test
    fun `onConnectedToServer does nothing`() = runTest {
        val plugin = FloconNetworkPluginImpl()
        plugin.onConnectedToServer()
    }

    @Test
    fun `logRequest does nothing`() {
        val plugin = FloconNetworkPluginImpl()
        plugin.logRequest(FloconNetworkCallRequest(
            floconNetworkCallRequestToJson = "{\"id\":\"1\"}"
        ))
    }

    @Test
    fun `logResponse does nothing`() {
        val plugin = FloconNetworkPluginImpl()
        plugin.logResponse(FloconNetworkCallResponse(
            floconNetworkCallResponseToJson = "{\"id\":\"1\"}"
        ))
    }

    @Test
    fun `logWebSocket does nothing`() = runTest {
        val plugin = FloconNetworkPluginImpl()
        plugin.logWebSocket(FloconWebSocketEvent(
            floconNetworkWebSocketEventToJson = "{\"id\":\"1\"}"
        ))
    }

    @Test
    fun `registerWebSocketMockListener does nothing`() {
        val plugin = FloconNetworkPluginImpl()
        plugin.registerWebSocketMockListener("id", object : FloconWebSocketMockListener {
            override fun onMessage(message: String) {}
        })
    }
}
