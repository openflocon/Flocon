package io.github.openflocon.flocon

import kotlinx.coroutines.flow.StateFlow

/**
 * Keeps what the plugins log in memory while no desktop app is reachable, to send it once one is.
 *
 * Flocon always tries the default address (localhost on an emulator). Use it when the address of
 * the desktop app can only be known later, then give it with [connect]: both are tried.
 *
 * @param bufferCapacity number of messages kept while not connected. Above it, the oldest are dropped.
 * @param address the IP address of a computer to also try when Flocon starts, for example one with a
 * fixed IP. No port nor scheme: the port is always 9023.
 * @param rememberLastAddress keeps the last address Flocon connected to (given here or to [connect]) in the
 * app preferences, and tries it again at the next launch. It wins over [address].
 */
@OptIn(FloconInternalApi::class)
fun FloconConfiguration.connectLater(
    bufferCapacity: Int = 5_000,
    address: String? = null,
    rememberLastAddress: Boolean = true,
) {
    keepMessages(capacity = bufferCapacity)
    connectAtStart(
        initialAddress = address,
        store = if (rememberLastAddress) floconAddressStore(context) else null,
    )
}

/** The state of the connection to the desktop app: `Connected` as soon as one address is connected. */
@OptIn(FloconInternalApi::class)
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
 * No port nor scheme: the port is always 9023, anything else is a [FloconConnectResult.InvalidAddress].
 * @return [FloconConnectResult.Connected], or why it could not connect. The messages are kept
 * in every case, call it again to retry.
 */
@OptIn(FloconInternalApi::class)
suspend fun Flocon.Companion.connect(address: String): FloconConnectResult = connectToServer(address)
