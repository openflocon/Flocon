package io.github.openflocon.flocon.okhttp.websocket

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

object FloconWebSocket {

    fun send(webSocket: WebSocket, text: String): Boolean = webSocket.send(text = text)

    fun send(webSocket: WebSocket, bytes: ByteString): Boolean = webSocket.send(bytes = bytes)

    // only forwards the events to the wrapped listener
    data class Listener(val listener: WebSocketListener, val id: String) : WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) =
            listener.onClosed(webSocket, code, reason)

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) =
            listener.onClosing(webSocket, code, reason)

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) =
            listener.onFailure(webSocket, t, response)

        override fun onMessage(webSocket: WebSocket, text: String) =
            listener.onMessage(webSocket, text)

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) =
            listener.onMessage(webSocket, bytes)

        override fun onOpen(webSocket: WebSocket, response: Response) =
            listener.onOpen(webSocket, response)
    }
}

fun WebSocket.sendWithFlocon(text: String) = this.send(text) // simple method call, adds no flocon override
fun WebSocket.sendWithFlocon(bytes: ByteString) = this.send(bytes) // simple method call, adds no flocon override

fun WebSocketListener.listenWithFlocon(id: String): WebSocketListener {
    return this
}
