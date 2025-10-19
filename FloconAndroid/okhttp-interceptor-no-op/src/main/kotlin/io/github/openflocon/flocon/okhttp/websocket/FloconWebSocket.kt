package io.github.openflocon.flocon.okhttp.websocket

import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


fun WebSocket.sendWithFlocon(text: String) = this.send(text) // simple method call, adds no flocon override
fun WebSocket.sendWithFlocon(bytes: ByteString) = this.send(bytes) // simple method call, adds no flocon override

fun WebSocketListener.listenWithFlocon(id: String): WebSocketListener {
    return this
}