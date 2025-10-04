package io.github.openflocon.flocon.okhttp.websocket

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketEvent
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

object FloconWebSocket {

    fun send(webSocket: WebSocket, text: String) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            FloconWebSocketEvent(
                websocketUrl = webSocket.request().url.toString(),
                timeStamp = System.currentTimeMillis(),
                event = FloconWebSocketEvent.Event.SendMessage,
                message = text,
            )

        )
        webSocket.send(text = text)
    }

    fun send(webSocket: WebSocket, bytes: ByteString) {
        FloconApp.instance?.client?.networkPlugin?.logWebSocket(
            FloconWebSocketEvent(
                websocketUrl = webSocket.request().url.toString(),
                timeStamp = System.currentTimeMillis(),
                event = FloconWebSocketEvent.Event.SendMessage,
                message = bytes.toString(), // not sure
            )
        )
        webSocket.send(bytes = bytes)
    }

    data class Listener(val listener: WebSocketListener) : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            FloconApp.instance?.client?.networkPlugin?.logWebSocket(
                FloconWebSocketEvent(
                    websocketUrl = webSocket.request().url.toString(),
                    timeStamp = System.currentTimeMillis(),
                    event = FloconWebSocketEvent.Event.Closed,
                )
            )
            listener.onClosed(webSocket, code, reason)
            super.onClosed(webSocket, code, reason)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            FloconApp.instance?.client?.networkPlugin?.logWebSocket(
                FloconWebSocketEvent(
                    websocketUrl = webSocket.request().url.toString(),
                    timeStamp = System.currentTimeMillis(),
                    event = FloconWebSocketEvent.Event.Closing,
                )
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
                FloconWebSocketEvent(
                    websocketUrl = webSocket.request().url.toString(),
                    timeStamp = System.currentTimeMillis(),
                    event = FloconWebSocketEvent.Event.Error,
                    error = t,
                )
            )
            listener.onFailure(webSocket, t, response)
            super.onFailure(webSocket, t, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            FloconApp.instance?.client?.networkPlugin?.logWebSocket(
                FloconWebSocketEvent(
                    websocketUrl = webSocket.request().url.toString(),
                    timeStamp = System.currentTimeMillis(),
                    event = FloconWebSocketEvent.Event.ReceiveMessage,
                    message = text,
                )
            )
            listener.onMessage(webSocket, text)
            super.onMessage(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            FloconApp.instance?.client?.networkPlugin?.logWebSocket(
                FloconWebSocketEvent(
                    websocketUrl = webSocket.request().url.toString(),
                    timeStamp = System.currentTimeMillis(),
                    event = FloconWebSocketEvent.Event.ReceiveMessage,
                    message = bytes.toString(), // TODO not sure
                )
            )
            listener.onMessage(webSocket, bytes)
            super.onMessage(webSocket, bytes)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            FloconApp.instance?.client?.networkPlugin?.logWebSocket(
                FloconWebSocketEvent(
                    websocketUrl = webSocket.request().url.toString(),
                    timeStamp = System.currentTimeMillis(),
                    event = FloconWebSocketEvent.Event.Open,
                )
            )
            listener.onOpen(webSocket, response)
            super.onOpen(webSocket, response)
        }

    }
}

fun WebSocket.sendWithFlocon(text: String) = FloconWebSocket.send(this, text)
fun WebSocket.sendWithFlocon(bytes: ByteString) = FloconWebSocket.send(this, bytes)

fun WebSocketListener.listenWithFlocon(): WebSocketListener {
    return FloconWebSocket.Listener(this)
}