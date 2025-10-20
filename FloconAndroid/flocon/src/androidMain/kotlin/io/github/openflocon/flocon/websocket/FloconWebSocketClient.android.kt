package io.github.openflocon.flocon.websocket

import kotlin.Throws
import io.github.openflocon.flocon.FloconLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.EOFException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal actual fun buildFloconWebSocketClient(): FloconWebSocketClient {
    return FloconWebSocketClientImpl()
}

internal class FloconWebSocketClientImpl : FloconWebSocketClient {

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null

    private val queue = MessageQueue(capacity = 200)

    @Throws(Throwable::class)
    override suspend fun connect(
        address: String,
        port: Int,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit,
    ) {
        // if already connected, do nothing
        if (webSocket != null) return

        return suspendCancellableCoroutine { continuation ->
            val connectionListener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    FloconLogger.log("✅ WebSocket Connection Opened!")

                    this@FloconWebSocketClientImpl.webSocket = webSocket
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    FloconLogger.log("WEBSOCKET <----- Receiving $text")

                    onMessageReceived(text)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    FloconLogger.logError("❌ WebSocket Connection Failed: ${t.message}", t)
                    if(response == null && t is EOFException) {
                        disconnect()
                        onClosed()
                    }
                    // Connection failed, resume the coroutine with an exception
                    if (continuation.isActive) {
                        continuation.resumeWithException(t)
                    }
                }
            }

            val request = Request.Builder()
                .url("ws://$address:$port")
                .build()

            val newWebSocket = client.newWebSocket(request, connectionListener)

            // if the coroutine is canceled
            // ensure we close the websocket.
            continuation.invokeOnCancellation {
                FloconLogger.log("Coroutine cancelled. Closing WebSocket.")
                newWebSocket.cancel()
            }
        }
    }

    override suspend fun sendMessage(message: String): Boolean {
        // Once the connection is established, the main listener (socketListener)
        // will be used for messages, closures, etc.
        // We attach the main listener to the socket after a successful connection.
        webSocket?.let {
            // This is a workaround to replace the initial listener with the permanent one.
            // OkHttp doesn't provide a direct method for this, so we have to handle it ourselves.
            // A simpler approach would be to use a single listener and check the connection state.
            // In our case, the 'connectionListener' is disposable, and the 'socketListener' takes over implicitly.
        } ?: run {
            // if not connected yet, just append the message
            queue.add(message)
        }
        FloconLogger.log("WEBSOCKET ----> send $message")
        return webSocket?.send(message) ?: false
    }

    override suspend fun sendPendingMessages() {
        var message = queue.poll()
        while (message != null) {
            sendMessage(message)
            message = queue.poll()
        }
    }

    override fun disconnect() {
        webSocket?.close(1000, "Client disconnecting")
        webSocket = null
    }
}