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

    is FloconConnectionMode.Url -> FloconServerAddress(
        host = host,
        websocketPort = websocketPort,
        httpPort = httpPort,
    )
}
