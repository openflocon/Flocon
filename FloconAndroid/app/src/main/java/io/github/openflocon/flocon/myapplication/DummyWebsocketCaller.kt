package io.github.openflocon.flocon.myapplication

import io.github.openflocon.flocon.okhttp.websocket.sendWithFlocon
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import io.github.openflocon.flocon.okhttp.websocket.listenWithFlocon

class DummyWebsocketCaller(val client: OkHttpClient) {

    private var ws: WebSocket? = null

    fun connectToWebsocket() {
        val request = Request.Builder()
            .url("wss://ws.postman-echo.com/raw")
            .build()
        val listener = object : WebSocketListener() {
            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                super.onClosed(webSocket, code, reason)
            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                super.onClosing(webSocket, code, reason)
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                super.onFailure(webSocket, t, response)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
            }
        }
        this.ws = client.newWebSocket(
            request,
            listener.listenWithFlocon(),
        )
    }

    fun send(text: String) {
        ws?.sendWithFlocon("\"$text\"")
    }

}
