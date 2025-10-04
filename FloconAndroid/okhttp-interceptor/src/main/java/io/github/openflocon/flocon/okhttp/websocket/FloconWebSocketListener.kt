package io.github.openflocon.flocon.okhttp.websocket

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.network.WebSocketEvent
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

data class FloconWebSocketListener(val listener: WebSocketListener) : WebSocketListener() {

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            event = WebSocketEvent.Closed,
        )
        listener.onClosed(webSocket, code, reason)
        super.onClosed(webSocket, code, reason)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            event = WebSocketEvent.Closing,
        )
        listener.onClosing(webSocket, code, reason)
        super.onClosing(webSocket, code, reason)
    }

    override fun onFailure(
        webSocket: WebSocket,
        t: Throwable,
        response: Response?
    ) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            event = WebSocketEvent.Error,
            error = t,
        )
        listener.onFailure(webSocket, t, response)
        super.onFailure(webSocket, t, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            event = WebSocketEvent.ReceiveMessage,
            message = text,
        )
        listener.onMessage(webSocket, text)
        super.onMessage(webSocket, text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            event = WebSocketEvent.ReceiveMessage,
            message = bytes.toString(), // TODO not sure
        )
        listener.onMessage(webSocket, bytes)
        super.onMessage(webSocket, bytes)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            event = WebSocketEvent.Open,
        )
        listener.onOpen(webSocket, response)
        super.onOpen(webSocket, response)
    }

}


fun WebSocketListener.listenWithFlocon(): WebSocketListener {
    return FloconWebSocketListener(this)
}