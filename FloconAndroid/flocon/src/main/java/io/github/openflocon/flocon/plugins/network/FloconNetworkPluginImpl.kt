package io.github.openflocon.flocon.plugins.network

import android.content.Context
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.network.mapper.floconNetworkCallRequestToJson
import io.github.openflocon.flocon.plugins.network.mapper.floconNetworkCallResponseToJson
import io.github.openflocon.flocon.plugins.network.mapper.parseBadQualityConfig
import io.github.openflocon.flocon.plugins.network.mapper.parseMockResponses
import io.github.openflocon.flocon.plugins.network.mapper.toJsonObject
import io.github.openflocon.flocon.plugins.network.mapper.writeMockResponsesToJson
import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

private const val FLOCON_NETWORK_MOCKS_JSON = "flocon_network_mocks.json"
private const val FLOCON_NETWORK_BAD_CONFIG_JSON = "flocon_network_bad_config.json"

class FloconNetworkPluginImpl(
    private val context: Context,
    private var sender: FloconMessageSender,
    private val coroutineScope: CoroutineScope,
) : FloconNetworkPlugin {

    override val mocks = CopyOnWriteArrayList<MockNetworkResponse>(loadMocksFromFile())
    private val _badQualityConfig = AtomicReference<BadQualityConfig?>(loadBadNetworkConfig())

    override val badQualityConfig: BadQualityConfig?
        get() = _badQualityConfig.get()

    override fun logRequest(request: FloconNetworkCallRequest) {
        sender.send(
            plugin = Protocol.FromDevice.Network.Plugin,
            method = Protocol.FromDevice.Network.Method.LogNetworkCallRequest,
            body = floconNetworkCallRequestToJson(request).toString(),
        )
    }

    override fun logResponse(response: FloconNetworkCallResponse) {
        coroutineScope.launch(Dispatchers.IO) {
            delay(200) // to be sure the request is handled before the response, in case of mocks or direct connection refused
            sender.send(
                plugin = Protocol.FromDevice.Network.Plugin,
                method = Protocol.FromDevice.Network.Method.LogNetworkCallResponse,
                body = floconNetworkCallResponseToJson(response).toString(),
            )
        }
    }

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender
    ) {
        when (messageFromServer.method) {
            Protocol.ToDevice.Network.Method.SetupMocks -> {
                val setup = parseMockResponses(messageFromServer.body)
                mocks.clear()
                mocks.addAll(setup)
                saveMocksToFile(mocks)
            }

            Protocol.ToDevice.Network.Method.SetupBadNetworkConfig -> {
                val config = parseBadQualityConfig(messageFromServer.body)
                _badQualityConfig.set(config)
                saveBadNetworkConfig(config)
            }
        }
    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
        // no op
    }

    private fun saveMocksToFile(mocks: CopyOnWriteArrayList<MockNetworkResponse>) {
        try {
            val file = File(context.filesDir, FLOCON_NETWORK_MOCKS_JSON)
            val jsonString = writeMockResponsesToJson(mocks).toString(2)
            FileOutputStream(file).use {
                it.write(jsonString.toByteArray())
            }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveMocksToFile", t)
        }
    }

    private fun loadMocksFromFile(): List<MockNetworkResponse> {
        return try {
            val file = File(context.filesDir, FLOCON_NETWORK_MOCKS_JSON)
            if (!file.exists()) {
                return emptyList()
            }

            val jsonString = FileInputStream(file).use {
                it.readBytes().toString(Charsets.UTF_8)
            }
            parseMockResponses(jsonString)
        } catch (t: Throwable) {
            FloconLogger.logError("issue in loadMocksFromFile", t)
            emptyList()
        }
    }

    private fun loadBadNetworkConfig(): BadQualityConfig? {
        return try {
            val file = File(context.filesDir, FLOCON_NETWORK_BAD_CONFIG_JSON)
            if (!file.exists()) {
                return null
            }

            val jsonString = FileInputStream(file).use {
                it.readBytes().toString(Charsets.UTF_8)
            }
            parseBadQualityConfig(jsonString)
        } catch (t: Throwable) {
            FloconLogger.logError("issue in loadBadNetworkConfig", t)
            null
        }
    }

    private fun saveBadNetworkConfig(config: BadQualityConfig?) {
        try {
            val file = File(context.filesDir, FLOCON_NETWORK_BAD_CONFIG_JSON)
            if (config == null) {
                file.delete()
            } else {
                val jsonString = toJsonObject(config).toString(2)
                FileOutputStream(file).use {
                    it.write(jsonString.toByteArray())
                }
            }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveBadNetworkConfig", t)
        }
    }

}