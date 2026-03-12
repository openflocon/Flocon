package io.github.openflocon.flocon.network.core.noop.plugin

import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.network.core.FloconNetworkPlugin
import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.network.core.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.network.core.model.FloconWebSocketEvent
import io.github.openflocon.flocon.network.core.model.FloconWebSocketMockListener
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse

internal class FloconNetworkPluginImpl : FloconPlugin, FloconNetworkPlugin {
    override val key: String = "NETWORK"
    override val mocks: Collection<MockNetworkResponse> = emptyList()
    override val badQualityConfig: BadQualityConfig? = null

    override suspend fun onMessageReceived(method: String, body: String) = Unit // No op
    override suspend fun onConnectedToServer() = Unit // No op

    override fun logRequest(request: FloconNetworkCallRequest) = Unit // No op
    override fun logResponse(response: FloconNetworkCallResponse) = Unit // No op

    override suspend fun logWebSocket(event: FloconWebSocketEvent) = Unit // No op
    override suspend fun registerWebSocketMockListener(
        id: String,
        listener: FloconWebSocketMockListener
    ) = Unit // No op

    companion object {
        var plugin: FloconNetworkPlugin? = null
    }
}