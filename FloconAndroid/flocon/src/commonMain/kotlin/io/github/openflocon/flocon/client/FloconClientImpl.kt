package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconConnectionMode
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.bluetooth.buildFloconBluetoothTransport
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.core.getAppInfos
import io.github.openflocon.flocon.getServerHost
import io.github.openflocon.flocon.model.FloconFileInfo
import io.github.openflocon.flocon.model.FloconMessageToServer
import io.github.openflocon.flocon.utils.currentTimeMillis
import io.github.openflocon.flocon.websocket.FloconHttpClient
import io.github.openflocon.flocon.websocket.FloconWebSocketClient
import io.github.openflocon.flocon.websocket.buildFloconHttpClient
import io.github.openflocon.flocon.websocket.buildFloconWebSocketClient
import io.github.openflocon.flocondesktop.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile
import kotlinx.serialization.modules.EmptySerializersModule

class FloconClient internal constructor(
    private val context: FloconContext,
    private val scope: CoroutineScope,
) : FloconApp.Client, FloconMessageSender, FloconFileSender {

    private val appInstance by lazy { currentTimeMillis() }
    private val appInfos by lazy { getAppInfos(context) }
    private val versionName by lazy { BuildConfig.APP_VERSION }
    private val adbHost by lazy { getServerHost(context) }

    @Volatile
    private var connectionMode: FloconConnectionMode = FloconConnectionMode.Adb

    // resolved on every use, so a mode set later is picked up by the next connection attempt
    private val address: FloconServerAddress
        get() = connectionMode.resolve(adbHost = { adbHost })

    private var webSocketClient: FloconWebSocketClient = buildFloconWebSocketClient()
    private var httpClient: FloconHttpClient = buildFloconHttpClient()

    private var encoder = FloconEncoder(EmptySerializersModule())

    // Better way ?
    fun setupEncoder(encoder: FloconEncoder) {
        this.encoder = encoder
    }

    fun setupConnection(mode: FloconConnectionMode) {
        this.connectionMode = mode
        if (mode == FloconConnectionMode.Bluetooth) {
            // one link carries the messages and the files
            val transport = buildFloconBluetoothTransport(context)
            webSocketClient = transport
            httpClient = transport
        }
    }

    /**
     * Switches the target of an already started client: the current session (if any) is closed,
     * which restarts the connection loop against the new address.
     */
    suspend fun updateConnection(mode: FloconConnectionMode) {
        setupConnection(mode)
        webSocketClient.disconnect()
    }

    @Throws(Throwable::class)
    override suspend fun connect(
        onClosed: () -> Unit,
        onMessageReceived: (message: String) -> Unit
    ) {
        webSocketClient.connect(
            address = address.host,
            port = address.websocketPort,
            onMessageReceived = onMessageReceived,
            onClosed = onClosed,
        )
    }

    override suspend fun disconnect() {
        webSocketClient.disconnect()
    }

    override fun send(
        plugin: String,
        method: String,
        body: String,
    ) {
        scope.launch {
            webSocketClient.sendMessage(
                encoder.encode(
                    FloconMessageToServer(
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
                )
            )
        }
    }

    override fun send(
        file: FloconFile,
        infos: FloconFileInfo,
    ) {
        scope.launch {
            httpClient.send(
                address = address.host,
                port = address.httpPort,
                file = file,
                infos = infos,
                deviceId = appInfos.deviceId,
                appPackageName = appInfos.appPackageName,
                appInstance = appInstance,
            )
        }
    }

    override fun sendPendingMessages() {
        scope.launch {
            webSocketClient.sendPendingMessages()
        }
    }
}
