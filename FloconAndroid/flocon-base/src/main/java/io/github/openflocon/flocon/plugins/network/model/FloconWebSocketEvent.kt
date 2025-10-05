package io.github.openflocon.flocon.plugins.network.model

class FloconWebSocketEvent(
    val websocketUrl: String,
    val event: Event,
    val size: Long = 0L,
    val message: String? = null,
    val error: Throwable? = null,
    val timeStamp: Long = System.currentTimeMillis(),
) {
    enum class Event {
        Closed,
        Closing,
        Error,
        ReceiveMessage,
        SendMessage,
        Open,
    }
}