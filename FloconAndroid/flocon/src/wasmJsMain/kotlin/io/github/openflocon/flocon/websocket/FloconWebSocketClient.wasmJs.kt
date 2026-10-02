package io.github.openflocon.flocon.websocket

internal actual fun buildFloconWebSocketClient(): FloconWebSocketClient = object : FloconWebSocketClient {
    override suspend fun connect(
        address: String,
        port: Int,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit
    ) {
    }

    override suspend fun sendPendingMessages() {
    }

    override suspend fun sendMessage(message: String): Boolean = false

    override suspend fun disconnect() {
    }
}
