package io.github.openflocon.flocon.plugins.network

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.network.mapper.floconNetworkCallRequestToJson
import io.github.openflocon.flocon.plugins.network.mapper.floconNetworkCallResponseToJson
import io.github.openflocon.flocon.plugins.network.mapper.floconNetworkWebSocketEventToJson
import io.github.openflocon.flocon.plugins.network.mapper.parseBadQualityConfig
import io.github.openflocon.flocon.plugins.network.mapper.parseMockResponses
import io.github.openflocon.flocon.plugins.network.mapper.parseWebSocketMockMessage
import io.github.openflocon.flocon.plugins.network.mapper.webSocketIdsToJsonArray
import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketEvent
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketMockListener
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val FLOCON_NETWORK_MOCKS_JSON = "flocon_network_mocks.json"
internal const val FLOCON_NETWORK_BAD_CONFIG_JSON = "flocon_network_bad_config.json"

internal interface FloconNetworkDataSource {
    fun saveMocksToFile(mocks: List<MockNetworkResponse>)
    fun loadMocksFromFile() : List<MockNetworkResponse>
    fun saveBadNetworkConfig(config: BadQualityConfig?)
    fun loadBadNetworkConfig(): BadQualityConfig?
}

internal expect fun buildFloconNetworkDataSource(context: FloconContext): FloconNetworkDataSource

internal class FloconNetworkPluginImpl(
    private val context: FloconContext,
    private var sender: FloconMessageSender,
    private val coroutineScope: CoroutineScope,
) : FloconPlugin, FloconNetworkPlugin {

    private val dataSource = buildFloconNetworkDataSource(context)

    private val websocketListeners = MutableStateFlow<Map<String, FloconWebSocketMockListener>>(emptyMap())

    private val _mocks = MutableStateFlow<List<MockNetworkResponse>>(dataSource.loadMocksFromFile())
    override val mocks : List<MockNetworkResponse>
        get() = _mocks.value

    private val _badQualityConfig = MutableStateFlow<BadQualityConfig?>(dataSource.loadBadNetworkConfig())

    override val badQualityConfig: BadQualityConfig?
        get() {
             return _badQualityConfig.value
        }

    override fun logRequest(request: FloconNetworkCallRequest) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.Network.Plugin,
                method = Protocol.FromDevice.Network.Method.LogNetworkCallRequest,
                body = request.floconNetworkCallRequestToJson(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Network json mapping error", t)
        }
    }

    override fun logResponse(response: FloconNetworkCallResponse) {
        coroutineScope.launch {
            delay(200) // to be sure the request is handled before the response, in case of mocks or direct connection refused
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

    override fun logWebSocket(
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

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {
        when (messageFromServer.method) {
            Protocol.ToDevice.Network.Method.SetupMocks -> {
                val setup = parseMockResponses(jsonString = messageFromServer.body)
                _mocks.update { setup }
                dataSource.saveMocksToFile(mocks)
            }

            Protocol.ToDevice.Network.Method.SetupBadNetworkConfig -> {
                val config = parseBadQualityConfig(jsonString = messageFromServer.body)
                _badQualityConfig.update { config }
                dataSource.saveBadNetworkConfig(config)
            }

            Protocol.ToDevice.Network.Method.WebsocketMockMessage -> {
                val message = parseWebSocketMockMessage(jsonString = messageFromServer.body)
                if(message != null) {
                    websocketListeners.value[message.id]?.onMessage(message.message)
                }
            }
        }
    }

    override fun onConnectedToServer() {
        updateWebSocketIds()
    }

    override fun registerWebSocketMockListener(
        id: String,
        listener: FloconWebSocketMockListener
    ) {
        websocketListeners.update {
            it + (id to listener)
        }
        updateWebSocketIds()
    }

    private fun updateWebSocketIds() {
        sender.send(
            plugin = Protocol.FromDevice.Network.Plugin,
            method = Protocol.FromDevice.Network.Method.RegisterWebSocketIds,
            body = webSocketIdsToJsonArray(ids = websocketListeners.value.keys),
        )
    }

}