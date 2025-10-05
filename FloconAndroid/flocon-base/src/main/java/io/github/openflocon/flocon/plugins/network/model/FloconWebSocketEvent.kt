package io.github.openflocon.flocon.plugins.network.model

class FloconWebSocketEvent(
    val websocketUrl: String,
    val timeStamp: Long,
    val event: Event,
    val size: Long,
    val message: String? = null,
    val error: Throwable? = null,
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