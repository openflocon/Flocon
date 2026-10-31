package io.github.openflocon.flocon.websocket

internal expect fun buildFloconWebSocketClient() : FloconWebSocketClient

interface FloconWebSocketClient {

    @Throws(Throwable::class)
    suspend fun connect(
        address: String,
        port: Int,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit,
    )

    /** Returns `false` if the message could not be sent, for example when not connected. */
    suspend fun sendMessage(message: String): Boolean

    suspend fun disconnect()
}

