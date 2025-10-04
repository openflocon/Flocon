package io.github.openflocon.flocon.plugins.network

import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse

enum class WebSocketEvent {
    Closed,
    Closing,
    Error,
    ReceiveMessage,
    SendMessage,
    Open,
}

interface FloconNetworkPlugin : FloconPlugin {
    val mocks: Collection<MockNetworkResponse>
    val badQualityConfig: BadQualityConfig?

    fun logRequest(request: FloconNetworkCallRequest)
    fun logResponse(response: FloconNetworkCallResponse)

    fun logWebSocket(
        event: WebSocketEvent,
        message: String? = null,
        error: Throwable? = null,
    )

}
