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

    suspend fun sendPendingMessages()

    suspend fun sendMessage(message: String): Boolean

    suspend fun disconnect()
}

