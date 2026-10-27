package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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

    @Volatile
    private var server: FloconServer? = null
    private val address get() = (server ?: defaultServer()).host

    private val webSocketClient: FloconWebSocketClient = buildFloconWebSocketClient()
    private val httpClient: FloconHttpClient = buildFloconHttpClient()

    private var encoder = FloconEncoder(EmptySerializersModule())

    /**
     * What the outbox task handles, in order: the connection changes go through the same channel
     * as the messages, so they are never reordered with them.
     */
    private sealed interface OutboxItem {
        class Message(val plugin: String, val method: String, val body: String) : OutboxItem
        class Connected(val handshakeBody: String) : OutboxItem
        data object Disconnected : OutboxItem
    }

    // Serial outbox: a coroutine per message would not keep them in order.
    private val items = Channel<OutboxItem>(Channel.UNLIMITED)

    // Changes each time a connection starts or ends, so that a late `onClosed` is ignored.
    private val connectionId = MutableStateFlow(0)

    private fun defaultServer() = FloconServer(getServerHost(context), FLOCON_WEBSOCKET_PORT)

    /**
     * Starts the outbox: until the connection is up, the messages are kept in memory.
     *
     * @param capacity number of messages kept while not connected, the oldest are dropped above it.
     */
    internal fun setupBuffer(capacity: Int) {
        val outbox = FloconOutbox(capacity = capacity, transport = { webSocketClient.sendMessage(it) })
        scope.launch {
            for (item in items) {
                when (item) {
                    is OutboxItem.Message -> outbox.enqueue(encode(item.plugin, item.method, item.body))
                    is OutboxItem.Connected -> outbox.connected(handshake = encode("bonjour", "bonjour", item.handshakeBody))
                    OutboxItem.Disconnected -> outbox.disconnected()
                }
            }
        }
    }

    /** Where to connect to, instead of the default address of the desktop app. */
    internal fun setServer(server: FloconServer) {
        this.server = server
    }

    /**
     * Sends the [handshakeBody] to the desktop app, then everything that was kept until now.
     * To call once connected.
     */
    internal fun onConnected(handshakeBody: String) {
        items.trySend(OutboxItem.Connected(handshakeBody))
    }

    private fun encode(plugin: String, method: String, body: String) = encoder.encode(
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

    // Better way ?
    fun setupEncoder(encoder: FloconEncoder) {
        this.encoder = encoder
    }

    @Throws(Throwable::class)
    override suspend fun connect(
        onClosed: () -> Unit,
        onMessageReceived: (message: String) -> Unit
    ) {
        connectionId.update { it + 1 }
        val id = connectionId.value
        val server = server ?: defaultServer()
        webSocketClient.connect(
            address = server.host,
            port = server.port,
            onMessageReceived = onMessageReceived,
            onClosed = {
                // once per connection, and not after an explicit disconnect()
                if (connectionId.compareAndSet(id, id + 1)) {
                    items.trySend(OutboxItem.Disconnected)
                    onClosed()
                }
            },
        )
    }

    override suspend fun disconnect() {
        connectionId.update { it + 1 }
        webSocketClient.disconnect()
        items.trySend(OutboxItem.Disconnected)
    }

    override fun send(
        plugin: String,
        method: String,
        body: String,
    ) {
        items.trySend(OutboxItem.Message(plugin = plugin, method = method, body = body))
    }

    override fun send(
        file: FloconFile,
        infos: FloconFileInfo,
    ) {
        scope.launch {
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
    }

    /** The messages kept while disconnected are sent by [onConnected], nothing to do here. */
    override fun sendPendingMessages() = Unit

    companion object {
        private const val FLOCON_WEBSOCKET_PORT = 9023
        private const val FLOCON_HTTP_PORT = 9024
    }
}