package io.github.openflocon.flocon.plugins.network

import android.content.Context
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.network.mapper.floconNetworkCallRequestToJson
import io.github.openflocon.flocon.plugins.network.mapper.floconNetworkCallResponseToJson
import io.github.openflocon.flocon.plugins.network.mapper.parseMockResponses
import io.github.openflocon.flocon.plugins.network.mapper.writeMockResponsesToJson
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

class FloconNetworkPluginImpl(
    private val context: Context,
    private var sender: FloconMessageSender,
    private val coroutineScope: CoroutineScope,
) : FloconNetworkPlugin {

    override val mocks = CopyOnWriteArrayList<MockNetworkResponse>(loadMocksFromFile())

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
        }
    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
        // no op
    }

    private fun saveMocksToFile(mocks: CopyOnWriteArrayList<MockNetworkResponse>) {
        try {
            val file = File(context.filesDir, "flocon_network_mocks.json")
            val jsonString = writeMockResponsesToJson(mocks).toString(2)
            FileOutputStream(file).use {
                it.write(jsonString.toByteArray())
            }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveMocksToFile", t)
        }
    }

    private fun loadMocksFromFile(): List<MockNetworkResponse> {
        /*
        return listOf(
            MockNetworkResponse(
                expectation = MockNetworkResponse.Expectation(
                    method = "*",
                    urlPattern = ".*todo.*",
                    pattern = Pattern.compile(".*"),
                ),
                response = MockNetworkResponse.Response(
                    httpCode = 201,
                    mediaType = "application/json",
                    body = "{ \"florent\" : \"champigny\" }",
                    delay = 0L,
                    headers = emptyMap(),
                )
            )
        )
         */

        return try {
            val file = File(context.filesDir, "flocon_network_mocks.json")
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
}