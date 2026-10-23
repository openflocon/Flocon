package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconConnectionMode

internal const val DEFAULT_WEBSOCKET_PORT = 9023
internal const val DEFAULT_HTTP_PORT = 9024

internal data class FloconServerAddress(
    val host: String,
    val websocketPort: Int,
    val httpPort: Int,
)

/**
 * Resolves where to dial the desktop. [adbHost] is only evaluated for [FloconConnectionMode.Adb].
 */
internal fun FloconConnectionMode.resolve(adbHost: () -> String): FloconServerAddress = when (this) {
    FloconConnectionMode.Adb -> FloconServerAddress(
        host = adbHost(),
        websocketPort = DEFAULT_WEBSOCKET_PORT,
        httpPort = DEFAULT_HTTP_PORT,
    )

    // nothing is dialled: the app listens, the transport doesn't use the address
    FloconConnectionMode.Bluetooth -> FloconServerAddress(
        host = "bluetooth",
        websocketPort = 0,
        httpPort = 0,
    )

    is FloconConnectionMode.Url -> FloconServerAddress(
        host = host.toBareHost(),
        websocketPort = websocketPort,
        httpPort = httpPort,
    )
}

/**
 * The ports and the scheme are added by the SDK: a pasted `ws://192.168.1.20:9023` would otherwise
 * be parsed with `ws` as the host and the port 80.
 */
internal fun String.toBareHost(): String {
    val withoutPath = trim().substringAfter("://").substringBefore('/')
    // more than one ':' is an ipv6 literal: leave it alone
    return if (withoutPath.count { it == ':' } == 1) withoutPath.substringBefore(':') else withoutPath
}
