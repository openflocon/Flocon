package io.github.openflocon.flocon.network.core.plugin

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.network.core.datasource.buildFloconNetworkDataSource
import io.github.openflocon.flocon.network.core.mapper.floconNetworkCallRequestToJson
import io.github.openflocon.flocon.network.core.mapper.floconNetworkCallResponseToJson
import io.github.openflocon.flocon.network.core.mapper.floconNetworkWebSocketEventToJson
import io.github.openflocon.flocon.network.core.mapper.parseBadQualityConfig
import io.github.openflocon.flocon.network.core.mapper.parseMockResponses
import io.github.openflocon.flocon.network.core.mapper.parseWebSocketMockMessage
import io.github.openflocon.flocon.network.core.mapper.webSocketIdsToJsonArray
import io.github.openflocon.flocon.pluginsold.network.FloconNetworkPlugin
import io.github.openflocon.flocon.pluginsold.network.model.BadQualityConfig
import io.github.openflocon.flocon.pluginsold.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.pluginsold.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.pluginsold.network.model.FloconWebSocketEvent
import io.github.openflocon.flocon.pluginsold.network.model.FloconWebSocketMockListener
import io.github.openflocon.flocon.pluginsold.network.model.MockNetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val FLOCON_NETWORK_MOCKS_JSON = "flocon_network_mocks.json"
internal const val FLOCON_NETWORK_BAD_CONFIG_JSON = "flocon_network_bad_config.json"

internal class FloconNetworkPluginImpl(
    context: FloconContext,
    private var sender: FloconMessageSender,
    private val coroutineScope: CoroutineScope
) : FloconPlugin, FloconNetworkPlugin {
    override val key: String = "NETWORK"

    private val dataSource = buildFloconNetworkDataSource(context)

    private val websocketListeners =
        MutableStateFlow<Map<String, FloconWebSocketMockListener>>(emptyMap())

    private val _mocks = MutableStateFlow(dataSource.loadMocksFromFile())
    override val mocks: List<MockNetworkResponse>
        get() = _mocks.value

    private val _badQualityConfig = MutableStateFlow(dataSource.loadBadNetworkConfig())

    override val badQualityConfig: BadQualityConfig?
        get() = _badQualityConfig.value

    override fun logRequest(request: FloconNetworkCallRequest) {
        try {
            coroutineScope.launch {
                sender.send(
                    plugin = Protocol.FromDevice.Network.Plugin,
                    method = Protocol.FromDevice.Network.Method.LogNetworkCallRequest,
                    body = request.floconNetworkCallRequestToJson(),
                )
            }
        } catch (t: Throwable) {
            FloconLogger.logError("Network json mapping error", t)
        }
    }

    override fun logResponse(response: FloconNetworkCallResponse) {
        coroutineScope.launch {
            delay(200) // to be sure the request is handled before the response
            try {
                sender.send(
                    plugin = Protocol.FromDevice.Network.Plugin,
                    method = Protocol.FromDevice.Network.Method.LogNetworkCallResponse,
                    body = response.floconNetworkCallResponseToJson(),
                )
            } catch (t: Throwable) {
                FloconLogger.logError("Network json mapping error", t)
            }
        }
    }

    override suspend fun logWebSocket(
        event: FloconWebSocketEvent,
    ) {
        coroutineScope.launch {
            try {
                sender.send(
                    plugin = Protocol.FromDevice.Network.Plugin,
                    method = Protocol.FromDevice.Network.Method.LogWebSocketEvent,
                    body = event.floconNetworkWebSocketEventToJson(),
                )
            } catch (t: Throwable) {
                FloconLogger.logError("Network json mapping error", t)
            }
        }
    }

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        when (method) {
            Protocol.ToDevice.Network.Method.SetupMocks -> {
                val setup = parseMockResponses(jsonString = body)
                _mocks.update { setup }
                dataSource.saveMocksToFile(mocks)
            }

            Protocol.ToDevice.Network.Method.SetupBadNetworkConfig -> {
                val config = parseBadQualityConfig(jsonString = body)
                _badQualityConfig.update { config }
                dataSource.saveBadNetworkConfig(config)
            }

            Protocol.ToDevice.Network.Method.WebsocketMockMessage -> {
                val message = parseWebSocketMockMessage(jsonString = body)
                if (message != null) {
                    websocketListeners.value[message.id]?.onMessage(message.message)
                }
            }
        }
    }

    override suspend fun onConnectedToServer() {
        updateWebSocketIds()
    }

    override suspend fun registerWebSocketMockListener(
        id: String,
        listener: FloconWebSocketMockListener
    ) {
        websocketListeners.update {
            it + (id to listener)
        }
        updateWebSocketIds()
    }

    private suspend fun updateWebSocketIds() {
        sender.send(
            plugin = Protocol.FromDevice.Network.Plugin,
            method = Protocol.FromDevice.Network.Method.RegisterWebSocketIds,
            body = webSocketIdsToJsonArray(ids = websocketListeners.value.keys),
        )
    }

    companion object {
        var plugin: FloconNetworkPlugin? = null
    }
}