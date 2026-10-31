package io.github.openflocon.flocon.network.core.model

import io.github.openflocon.flocon.utils.currentTimeMillis

class FloconWebSocketEvent(
    val websocketUrl: String,
    val event: Event,
    val size: Long = 0L,
    val message: String? = null,
    val error: Throwable? = null,
    val timeStamp: Long = currentTimeMillis(),
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
