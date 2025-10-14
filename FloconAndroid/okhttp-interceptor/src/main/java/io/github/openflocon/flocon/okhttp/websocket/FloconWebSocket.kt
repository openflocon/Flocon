package io.github.openflocon.flocon.okhttp.websocket

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.network.floconLogWebSocketEvent
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketEvent
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketMockListener
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.lang.ref.WeakReference

object FloconWebSocket {

    private fun log(
        webSocket: WebSocket,
        event: FloconWebSocketEvent.Event,
        message: String? = null,
        error: Throwable? = null,
    ) {
        val size = message?.toByteArray()?.size?.toLong()
        floconLogWebSocketEvent(
            FloconWebSocketEvent(
                websocketUrl = webSocket.request().url.toString(),
                event = event,
                message = message,
                error = error,
                size = size ?: 0L,
            )
        )
    }

    fun send(webSocket: WebSocket, text: String) : Boolean {
        log(
            webSocket = webSocket,
            event = FloconWebSocketEvent.Event.SendMessage,
            message = text,
        )
        return webSocket.send(text = text)
    }

    fun send(webSocket: WebSocket, bytes: ByteString) : Boolean {
        log(
            webSocket = webSocket,
            event = FloconWebSocketEvent.Event.SendMessage,
            message = bytes.toString(), // not sure
        )
        return webSocket.send(bytes = bytes)
    }

    data class Listener(val listener: WebSocketListener, val id: String) : WebSocketListener() {

        private var websocketRef : WeakReference<WebSocket>? = null

        init {
            FloconApp.instance?.client?.networkPlugin?.registerWebSocketMockListener(id = id, listener = object: FloconWebSocketMockListener {
                override fun onMessage(message: String) {
                    websocketRef?.get()?.let { websocket ->
                        onMessage(websocket, message)
                    }
                }
            })
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            log(
                webSocket = webSocket,
                event = FloconWebSocketEvent.Event.Closed,
            )
            listener.onClosed(webSocket, code, reason)
            super.onClosed(webSocket, code, reason)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            log(
                webSocket = webSocket,
                event = FloconWebSocketEvent.Event.Closing,
            )
            listener.onClosing(webSocket, code, reason)
            super.onClosing(webSocket, code, reason)
        }

        override fun onFailure(
            webSocket: WebSocket,
            t: Throwable,
            response: Response?
        ) {
            log(
                webSocket = webSocket,
                event = FloconWebSocketEvent.Event.Error,
                error = t,
            )
            listener.onFailure(webSocket, t, response)
            super.onFailure(webSocket, t, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            log(
                webSocket = webSocket,
                event = FloconWebSocketEvent.Event.ReceiveMessage,
                message = text,
            )
            listener.onMessage(webSocket, text)
            super.onMessage(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            log(
                webSocket = webSocket,
                event = FloconWebSocketEvent.Event.ReceiveMessage,
                message = bytes.toString(), // TODO not sure
            )
            listener.onMessage(webSocket, bytes)
            super.onMessage(webSocket, bytes)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            log(
                webSocket = webSocket,
                event = FloconWebSocketEvent.Event.Open,
            )
            websocketRef = WeakReference(webSocket)
            listener.onOpen(webSocket, response)
            super.onOpen(webSocket, response)
        }

    }
}

fun WebSocket.sendWithFlocon(text: String) = FloconWebSocket.send(this, text)
fun WebSocket.sendWithFlocon(bytes: ByteString) = FloconWebSocket.send(this, bytes)

fun WebSocketListener.listenWithFlocon(id: String): WebSocketListener {
    return FloconWebSocket.Listener(this, id = id)
}