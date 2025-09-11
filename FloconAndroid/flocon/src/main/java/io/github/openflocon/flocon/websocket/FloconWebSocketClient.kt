package io.github.openflocon.flocon.websocket

interface FloconWebSocketClient {

    @Throws(Throwable::class)
    suspend fun connect(
        address: String,
        port: Int,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit,
    )

    fun sendPendingMessages()

    fun sendMessage(message: String): Boolean

    fun disconnect()
}