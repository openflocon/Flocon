package io.github.openflocon.flocon.client

internal const val FLOCON_DEFAULT_WEBSOCKET_PORT = 9023
internal const val FLOCON_DEFAULT_HTTP_PORT = 9024

internal data class FloconServer(
    val host: String,
    val port: Int,
    val httpPort: Int,
)

/**
 * Reads the address of the desktop app: its IP address or host, with optional ports, such as
 * `192.168.1.20`, `my-mac.local`, `192.168.1.20:9100` (websocket port) or
 * `192.168.1.20:9100:9101` (websocket port, then HTTP port). An omitted port takes [defaultPort]
 * (websocket) or [defaultHttpPort], and a port in the address wins over them.
 *
 * @throws IllegalArgumentException if the address is empty, or has a scheme, a path, or an invalid port.
 */
internal fun parseFloconServer(
    address: String,
    defaultPort: Int = FLOCON_DEFAULT_WEBSOCKET_PORT,
    defaultHttpPort: Int = FLOCON_DEFAULT_HTTP_PORT,
): FloconServer {
    val trimmed = address.trim()
    val message = "Cannot read '$address', enter the IP address of the computer, such as 192.168.1.20, " +
        "192.168.1.20:$defaultPort or 192.168.1.20:$defaultPort:$defaultHttpPort " +
        "(ports between 1 and 65535, no scheme nor path)"
    require(trimmed.isNotEmpty() && trimmed.none { it == '/' || it == '?' || it.isWhitespace() }) { message }
    val parts = trimmed.split(':')
    require(parts.size <= 3) { message }
    val host = parts[0]
    require(host.isNotEmpty()) { message }
    fun port(index: Int, default: Int): Int {
        if (index >= parts.size) return default
        val digits = parts[index]
        require(digits.isNotEmpty() && digits.all { it in '0'..'9' } && digits.length <= 5) { message }
        return digits.toInt().also { require(it in 1..65535) { message } }
    }
    return FloconServer(host = host, port = port(1, defaultPort), httpPort = port(2, defaultHttpPort))
}
