package io.github.openflocon.flocon.plugins.network

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketEvent
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketMockListener
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse

fun floconLogWebSocketEvent(event: FloconWebSocketEvent) {
    FloconApp.instance?.client?.networkPlugin?.logWebSocket(event)
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
