package io.github.openflocon.flocon.client

internal const val FLOCON_DEFAULT_WEBSOCKET_PORT = 9023

internal data class FloconServer(
    val host: String,
    val port: Int,
)

/**
 * Reads the address of the desktop app: only its IP address or host, such as `192.168.1.20` or
 * `my-mac.local`. The port is always [FLOCON_DEFAULT_WEBSOCKET_PORT].
 *
 * @throws IllegalArgumentException if the address is empty, or has a scheme, a port or a path.
 */
internal fun parseFloconServer(address: String): FloconServer {
    val host = address.trim()
    require(host.isNotEmpty() && host.none { it == ':' || it == '/' || it == '?' || it.isWhitespace() }) {
        "Cannot read '$address', enter only the IP address of the computer, such as 192.168.1.20 " +
            "(no port: it is always $FLOCON_DEFAULT_WEBSOCKET_PORT)"
    }
    return FloconServer(host = host, port = FLOCON_DEFAULT_WEBSOCKET_PORT)
}
