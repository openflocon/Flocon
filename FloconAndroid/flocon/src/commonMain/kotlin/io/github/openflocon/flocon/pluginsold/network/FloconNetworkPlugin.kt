package io.github.openflocon.flocon.pluginsold.network

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.pluginsold.network.model.BadQualityConfig
import io.github.openflocon.flocon.pluginsold.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.pluginsold.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.pluginsold.network.model.FloconWebSocketEvent
import io.github.openflocon.flocon.pluginsold.network.model.FloconWebSocketMockListener
import io.github.openflocon.flocon.pluginsold.network.model.MockNetworkResponse

class FloconNetworkConfig {
    var badQualityConfig: BadQualityConfig? = null
    val mocks = mutableListOf<MockNetworkResponse>()
}

/**
 * Flocon Network Plugin.
 * Used to inspect HTTP/S and WebSocket calls.
 */
object FloconNetwork : FloconPluginFactory<FloconNetworkConfig, FloconNetworkPlugin> {
    override fun createConfig(): FloconNetworkConfig = TODO()
    override fun install(
        config: FloconNetworkConfig,
        app: FloconApp
    ): FloconNetworkPlugin = TODO()

    override val name: String = ""
}


interface FloconNetworkPlugin : FloconPlugin {
    val mocks: Collection<MockNetworkResponse>
    val badQualityConfig: BadQualityConfig?

    fun logRequest(request: FloconNetworkCallRequest)
    fun logResponse(response: FloconNetworkCallResponse)

    fun logWebSocket(
        event: FloconWebSocketEvent,
    )

    fun registerWebSocketMockListener(id: String, listener: FloconWebSocketMockListener)
}
