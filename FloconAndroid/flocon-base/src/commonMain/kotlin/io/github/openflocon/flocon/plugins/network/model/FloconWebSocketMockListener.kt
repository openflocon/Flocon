package io.github.openflocon.flocon.plugins.network.model

interface FloconWebSocketMockListener {
    fun onMessage(message: String)
}
