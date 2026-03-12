package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.getAppInfos
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.getServerHost
import io.github.openflocon.flocon.model.FloconFileInfo
import io.github.openflocon.flocon.model.FloconMessageToServer
import io.github.openflocon.flocon.model.toFloconMessageToServer
import io.github.openflocon.flocon.utils.currentTimeMillis
import io.github.openflocon.flocon.websocket.FloconHttpClient
import io.github.openflocon.flocon.websocket.FloconWebSocketClient
import io.github.openflocon.flocon.websocket.buildFloconHttpClient
import io.github.openflocon.flocon.websocket.buildFloconWebSocketClient
import io.github.openflocon.flocondesktop.BuildConfig

class FloconClient internal constructor(
    private val context: FloconContext
) : FloconApp.Client, FloconMessageSender, FloconFileSender {

    private val appInstance by lazy { currentTimeMillis() }
    private val appInfos by lazy { getAppInfos(context) }
    private val versionName by lazy { BuildConfig.APP_VERSION }
    private val address by lazy { getServerHost(context) }

    private val webSocketClient: FloconWebSocketClient = buildFloconWebSocketClient()
    private val httpClient: FloconHttpClient = buildFloconHttpClient()

    @Throws(Throwable::class)
    override suspend fun connect(
        onClosed: () -> Unit,
        onMessageReceived: (message: String) -> Unit
    ) {
        webSocketClient.connect(
            address = address,
            port = FLOCON_WEBSOCKET_PORT,
            onMessageReceived = onMessageReceived,
            onClosed = onClosed,
        )
    }

    override suspend fun disconnect() {
        webSocketClient.disconnect()
    }

    override suspend fun send(
        plugin: String,
        method: String,
        body: String,
    ) {
        webSocketClient.sendMessage(
            message = FloconMessageToServer(
                deviceId = appInfos.deviceId,
                plugin = plugin,
                body = body,
                appName = appInfos.appName,
                appPackageName = appInfos.appPackageName,
                method = method,
                deviceName = appInfos.deviceName,
                appInstance = appInstance,
                platform = appInfos.platform,
                versionName = versionName,
            )
                .toFloconMessageToServer(),
        )
    }

    @FloconMarker
    override suspend fun send(
        file: FloconFile,
        infos: FloconFileInfo,
    ) {
        httpClient.send(
            address = address,
            port = FLOCON_HTTP_PORT,
            file = file,
            infos = infos,

            deviceId = appInfos.deviceId,
            appPackageName = appInfos.appPackageName,
            appInstance = appInstance,
        )
    }

    override suspend fun sendPendingMessages() {
        webSocketClient.sendPendingMessages()
    }

    companion object {
        private const val FLOCON_WEBSOCKET_PORT = 9023
        private const val FLOCON_HTTP_PORT = 9024
    }
}