package io.github.openflocon.flocon.websocket

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

    fun disconnect()
}

