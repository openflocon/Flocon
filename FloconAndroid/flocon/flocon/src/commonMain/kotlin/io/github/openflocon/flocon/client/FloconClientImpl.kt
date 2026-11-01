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
import kotlinx.coroutines.Job
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

    private val httpClient: FloconHttpClient = buildFloconHttpClient()

    private var encoder = FloconEncoder(EmptySerializersModule())

    /**
     * What the outbox task handles, in order: the connection changes go through the same channel
     * as the messages, so they are never reordered with them.
     */
    private sealed interface OutboxItem {
        class Message(val plugin: String, val method: String, val body: String) : OutboxItem
        class Connected(val link: FloconLink, val handshakeBody: String) : OutboxItem
        class Disconnected(val link: FloconLink) : OutboxItem
        class AddLink(val link: FloconLink) : OutboxItem
        class RemoveLink(val link: FloconLink) : OutboxItem
    }

    // Serial outbox: a coroutine per message would not keep them in order.
    private val items = Channel<OutboxItem>(Channel.UNLIMITED)

    /** The connection to the default address of the desktop app, always tried. */
    internal val defaultLink: FloconLink = newLink(defaultServer())

    /** The connection to the address given with `Flocon.connect`, in addition to the default one. */
    @Volatile
    internal var extraLink: FloconLink? = null
        private set

    private val address
        get() = (extraLink?.takeIf { it.isConnected } ?: defaultLink.takeIf { it.isConnected } ?: defaultLink).server.host

    private fun defaultServer() = FloconServer(getServerHost(context), FLOCON_WEBSOCKET_PORT)

    private fun newLink(server: FloconServer): FloconLink {
        val link = FloconLink(server)
        items.trySend(OutboxItem.AddLink(link))
        return link
    }

    /**
     * Starts the outbox: until a connection is up, the messages are kept in memory.
     *
     * @param capacity number of messages kept while not connected, the oldest are dropped above it.
     */
    internal fun setupBuffer(capacity: Int) {
        val outbox = FloconOutbox(capacity = capacity)
        scope.launch {
            for (item in items) {
                when (item) {
                    is OutboxItem.Message -> outbox.enqueue(encode(item.plugin, item.method, item.body))
                    is OutboxItem.Connected -> outbox.connected(item.link.outboxLink, handshake = encode("bonjour", "bonjour", item.handshakeBody))
                    is OutboxItem.Disconnected -> outbox.disconnected(item.link.outboxLink)
                    is OutboxItem.AddLink -> outbox.add(item.link.outboxLink)
                    is OutboxItem.RemoveLink -> outbox.remove(item.link.outboxLink)
                }
            }
        }
    }

    /** Adds a connection to [server], next to the default one. It replaces the previous extra one. */
    internal fun addExtraLink(server: FloconServer): FloconLink {
        return newLink(server).also { extraLink = it }
    }

    /** Closes and forgets [link], which must be the extra one. */
    internal suspend fun removeExtraLink(link: FloconLink) {
        disconnect(link)
        if (extraLink === link) extraLink = null
        items.trySend(OutboxItem.RemoveLink(link))
    }

    /**
     * Sends the [handshakeBody] to the desktop app, then everything that was kept until now.
     * To call once [link] is connected.
     */
    internal fun onConnected(link: FloconLink, handshakeBody: String) {
        link.isConnected = true
        items.trySend(OutboxItem.Connected(link, handshakeBody))
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
    ) = connect(defaultLink, onClosed, onMessageReceived)

    override suspend fun disconnect() = disconnect(defaultLink)

    @Throws(Throwable::class)
    internal suspend fun connect(
        link: FloconLink,
        onClosed: () -> Unit,
        onMessageReceived: (message: String) -> Unit
    ) {
        link.connectionId.update { it + 1 }
        val id = link.connectionId.value
        link.webSocketClient.connect(
            address = link.server.host,
            port = link.server.port,
            onMessageReceived = onMessageReceived,
            onClosed = {
                // once per connection, and not after an explicit disconnect()
                if (link.connectionId.compareAndSet(id, id + 1)) {
                    link.isConnected = false
                    items.trySend(OutboxItem.Disconnected(link))
                    onClosed()
                }
            },
        )
    }

    internal suspend fun disconnect(link: FloconLink) {
        link.connectionId.update { it + 1 }
        link.isConnected = false
        link.webSocketClient.disconnect()
        items.trySend(OutboxItem.Disconnected(link))
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
/**
 * The connection to one address of the desktop app, with its own websocket.
 *
 * The messages are shared between the links, see [FloconOutbox].
 */
internal class FloconLink(val server: FloconServer) {
    val webSocketClient: FloconWebSocketClient = buildFloconWebSocketClient()
    val outboxLink = FloconOutbox.Link { webSocketClient.sendMessage(it) }

    // Changes each time a connection starts or ends, so that a late `onClosed` is ignored.
    val connectionId = MutableStateFlow(0)

    @Volatile
    var isConnected = false

    /** The task connecting, or retrying to connect, this link. */
    @Volatile
    var job: Job? = null
}
