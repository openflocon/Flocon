package io.github.openflocon.flocon.network.core.model

class FloconWebSocketEvent(
    val websocketUrl: String,
    val event: Event,
    val size: Long = 0L,
    val message: String? = null,
    val error: Throwable? = null,
    val timeStamp: Long = 0L,
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
