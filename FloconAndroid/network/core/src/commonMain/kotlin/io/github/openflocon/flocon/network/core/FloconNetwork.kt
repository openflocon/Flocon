package io.github.openflocon.flocon.network.core

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.network.core.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.network.core.model.FloconWebSocketEvent
import io.github.openflocon.flocon.network.core.model.FloconWebSocketMockListener
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse
import io.github.openflocon.flocon.network.core.plugin.FloconNetworkPluginImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

class FloconNetworkConfig : FloconPluginConfig {
    var badQualityConfig: BadQualityConfig? = null
    val mocks = mutableListOf<MockNetworkResponse>()
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

object FloconNetwork : FloconPluginFactory<FloconNetworkConfig, FloconNetworkPlugin> {
    override val name: String = "Network"
    override val pluginId: String = Protocol.ToDevice.Network.Plugin

    override fun createConfig(context: FloconContext) = FloconNetworkConfig()

    @OptIn(FloconMarker::class)
    override fun install(
        pluginConfig: FloconNetworkConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconNetworkPlugin {
        return FloconNetworkPluginImpl(
            context = floconConfig.context,
            sender = floconConfig.client as FloconMessageSender,
            coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            encoder = encoder
        )
            .also { FloconNetworkPluginImpl.plugin = it }
    }

}

@OptIn(FloconMarker::class)
val Flocon.Companion.networkPlugin: FloconNetworkPlugin
    get() = FloconNetworkPluginImpl.plugin ?: pluginNotInitialized("Network", NoOpNetworkPlugin())

private class NoOpNetworkPlugin : FloconNetworkPlugin {
    override val key: String = ""
    override val mocks: Collection<MockNetworkResponse> = emptyList()
    override val badQualityConfig: BadQualityConfig? = null
    override fun logRequest(request: FloconNetworkCallRequest) = Unit
    override fun logResponse(response: FloconNetworkCallResponse) = Unit
    override fun logWebSocket(event: FloconWebSocketEvent) = Unit
    override fun registerWebSocketMockListener(id: String, listener: FloconWebSocketMockListener) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

/**
 * The network plugin, or null if [FloconNetwork] is not installed (or Flocon not started yet).
 * Used by the interceptors, which must let the calls go through in that case.
 */
@OptIn(FloconMarker::class)
val Flocon.Companion.networkPluginOrNull: FloconNetworkPlugin?
    get() = FloconNetworkPluginImpl.plugin
