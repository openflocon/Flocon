package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FLOCON_DEFAULT_HTTP_PORT
import io.github.openflocon.flocon.client.FLOCON_DEFAULT_WEBSOCKET_PORT
import kotlinx.coroutines.flow.StateFlow

/** Options of [connect]. */
class FloconConnectConfig internal constructor() {
    /** Number of messages kept while not connected. Above it, the oldest are dropped. */
    var bufferCapacity: Int = 5_000

    /**
     * The IP address of a computer to also try when Flocon starts, for example one with a fixed IP.
     * `host`, `host:wsPort` or `host:wsPort:httpPort` (no scheme), such as `192.168.1.10:10023:10024`.
     * An omitted port takes [websocketPort] / [httpPort], and the ports of the address win over them
     * for this address, including the HTTP port used for the file uploads.
     */
    var address: String? = null

    /**
     * Keeps the last address Flocon connected to (given as [address] or to [connect]) in the app
     * preferences, and tries it again at the next launch. It wins over [address].
     */
    var rememberLastAddress: Boolean = true

    /**
     * The port of the websocket of Flocon Desktop. It must match the websocket port in the
     * desktop Settings. The default is 9023, which keeps working through `adb reverse`.
     */
    var websocketPort: Int = FLOCON_DEFAULT_WEBSOCKET_PORT

    /**
     * The port of the HTTP server of Flocon Desktop, used to upload files. It must match the
     * HTTP port in the desktop Settings. The default is 9024, which keeps working through `adb reverse`.
     */
    var httpPort: Int = FLOCON_DEFAULT_HTTP_PORT
}

/**
 * Keeps what the plugins log in memory while no desktop app is reachable, to send it once one is.
 *
 * Flocon always tries the default address (localhost on an emulator). Use it when the address of
 * the desktop app can only be known later, then give it with [connect]: both are tried.
 *
 * ```
 * connect {
 *     address = "192.168.1.20"
 * }
 * ```
 */
fun FloconConfiguration.connect(block: FloconConnectConfig.() -> Unit = {}) {
    val options = FloconConnectConfig().apply(block)
    keepMessages(capacity = options.bufferCapacity)
    connectAtStart(
        initialAddress = options.address,
        store = if (options.rememberLastAddress) floconAddressStore(context) else null,
        websocketPort = options.websocketPort,
        httpPort = options.httpPort,
    )
}

/** The state of the connection to the desktop app: `Connected` as soon as one address is connected. */
val Flocon.Companion.connectionState: StateFlow<FloconConnectionState>
    get() = connectionStateFlow

/**
 * Connects to the desktop app at the given address, in addition to the default one.
 *
 * Flocon always keeps trying the default address (localhost on an emulator), so this is
 * meant for what it cannot guess, such as the IP of the computer from a real device, typically
 * typed in a debug menu. Opens the connection, introduces the app to the desktop, then sends
 * everything that was logged and not sent yet, in the order it was logged, and sends the next
 * messages right away. If the connection is lost later, the messages are kept again and sent
 * on reconnection, which is retried every 3 seconds. Calling it again replaces the address.
 *
 * @param address the IP address (or host) of the computer running Flocon Desktop, such as `192.168.1.20`.
 * `host`, `host:wsPort` or `host:wsPort:httpPort` (no scheme nor path), such as `192.168.1.20:10023:10024`.
 * An omitted port takes the configured `websocketPort` / `httpPort` (9023 / 9024 by default), and the ports
 * of the address win over them for this address. Anything else is a [FloconConnectResult.InvalidAddress].
 * @return [FloconConnectResult.Connected], or why it could not connect. The messages are kept
 * in every case, call it again to retry.
 */
suspend fun Flocon.Companion.connect(address: String): FloconConnectResult = connectToServer(address)

/**
 * Closes the connection made with [connect] (or the address given at start), and forgets its address:
 * it is not tried again at the next launch. The default address (localhost on an emulator) is kept,
 * Flocon keeps trying it. The messages logged from now on are kept in memory, as when no desktop is reachable,
 * and sent when a connection is back. Does nothing when there is no such connection.
 */
suspend fun Flocon.Companion.disconnect() = disconnectFromServer()
