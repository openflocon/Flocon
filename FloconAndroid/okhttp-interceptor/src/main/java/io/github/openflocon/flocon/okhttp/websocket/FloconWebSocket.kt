package io.github.openflocon.flocon.okhttp.websocket

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.network.WebSocketEvent
import okhttp3.WebSocket
import okio.ByteString

object FloconWebSocket {
    fun send(webSocket: WebSocket, text: String) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            event = WebSocketEvent.SendMessage,
            message = text,
        )
        webSocket.send(text = text)
    }

    fun send(webSocket: WebSocket, bytes: ByteString) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            event = WebSocketEvent.SendMessage,
            message = bytes.toString(), // not sure
        )
        webSocket.send(bytes = bytes)
    }
}

fun WebSocket.sendWithFlocon(text: String) = FloconWebSocket.send(this, text)
fun WebSocket.sendWithFlocon(bytes: ByteString) = FloconWebSocket.send(this, bytes)