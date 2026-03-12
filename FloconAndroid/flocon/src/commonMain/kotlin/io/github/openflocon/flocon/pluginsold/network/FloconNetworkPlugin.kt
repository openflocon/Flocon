package io.github.openflocon.flocon.pluginsold.network

import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.pluginsold.network.model.BadQualityConfig
import io.github.openflocon.flocon.pluginsold.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.pluginsold.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.pluginsold.network.model.FloconWebSocketEvent
import io.github.openflocon.flocon.pluginsold.network.model.FloconWebSocketMockListener
import io.github.openflocon.flocon.pluginsold.network.model.MockNetworkResponse

class FloconNetworkConfig : FloconPluginConfig {
    var badQualityConfig: BadQualityConfig? = null
    val mocks = mutableListOf<MockNetworkResponse>()
}

interface FloconNetworkPlugin : FloconPlugin {
    val mocks: Collection<MockNetworkResponse>
    val badQualityConfig: BadQualityConfig?

    fun logRequest(request: FloconNetworkCallRequest)
    fun logResponse(response: FloconNetworkCallResponse)

    suspend fun logWebSocket(
        event: FloconWebSocketEvent,
    )

    suspend fun registerWebSocketMockListener(id: String, listener: FloconWebSocketMockListener)
}
