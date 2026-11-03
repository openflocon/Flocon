package io.github.openflocon.flocon.client

internal const val FLOCON_DEFAULT_WEBSOCKET_PORT = 9023
internal const val FLOCON_DEFAULT_HTTP_PORT = 9024

internal data class FloconServer(
    val host: String,
    val port: Int,
)

/**
 * Reads the address of the desktop app: its IP address or host, with an optional port, such as
 * `192.168.1.20`, `my-mac.local` or `192.168.1.20:9100`. Without a port, [defaultPort] is used,
 * and a port in the address wins over it.
 *
 * @throws IllegalArgumentException if the address is empty, or has a scheme, a path, or an invalid port.
 */
internal fun parseFloconServer(
    address: String,
    defaultPort: Int = FLOCON_DEFAULT_WEBSOCKET_PORT,
): FloconServer {
    val trimmed = address.trim()
    val message = "Cannot read '$address', enter the IP address of the computer, such as 192.168.1.20 " +
        "or 192.168.1.20:$defaultPort (port between 1 and 65535, no scheme nor path)"
    require(trimmed.isNotEmpty() && trimmed.none { it == '/' || it == '?' || it.isWhitespace() }) { message }
    val parts = trimmed.split(':')
    require(parts.size <= 2) { message }
    val host = parts[0]
    require(host.isNotEmpty()) { message }
    val port = if (parts.size == 2) {
        val digits = parts[1]
        require(digits.isNotEmpty() && digits.all { it in '0'..'9' } && digits.length <= 5) { message }
        digits.toInt().also { require(it in 1..65535) { message } }
    } else {
        defaultPort
    }
    return FloconServer(host = host, port = port)
}
