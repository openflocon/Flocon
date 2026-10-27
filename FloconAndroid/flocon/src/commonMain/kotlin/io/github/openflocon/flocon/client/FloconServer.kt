package io.github.openflocon.flocon.client

internal const val FLOCON_DEFAULT_WEBSOCKET_PORT = 9023

internal data class FloconServer(
    val host: String,
    val port: Int,
)

/**
 * Reads the address of the desktop app: `ws://192.168.1.20:9023`, `192.168.1.20:9023`
 * or `192.168.1.20` (port 9023).
 *
 * @throws IllegalArgumentException if the address is not readable, or uses a scheme other than `ws` / `http`.
 */
internal fun parseFloconServer(url: String): FloconServer {
    val trimmed = url.trim()
    val scheme = trimmed.substringBefore("://", missingDelimiterValue = "").lowercase()
    require(scheme.isEmpty() || scheme == "ws" || scheme == "http") {
        "Unsupported scheme '$scheme' in '$url': Flocon connects with ws://"
    }
    val authority = trimmed.substringAfter("://", trimmed)
        .substringBefore('/')
        .substringBefore('?')
    val host = authority.substringBeforeLast(':', missingDelimiterValue = authority)
    val portText = authority.substringAfterLast(':', missingDelimiterValue = "")
    require(host.isNotEmpty() && host.none { it == ':' || it.isWhitespace() }) {
        "Cannot read a host in '$url', expected something like ws://192.168.1.20:9023"
    }
    val port = if (portText.isEmpty()) {
        FLOCON_DEFAULT_WEBSOCKET_PORT
    } else {
        portText.toIntOrNull()?.takeIf { it in 1..65535 }
            ?: throw IllegalArgumentException("Invalid port '$portText' in '$url'")
    }
    return FloconServer(host = host, port = port)
}
