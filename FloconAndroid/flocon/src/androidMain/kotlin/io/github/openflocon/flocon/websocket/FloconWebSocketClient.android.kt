package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal actual fun buildFloconWebSocketClient(): FloconWebSocketClient {
    return FloconWebSocketClientImpl()
}

internal class FloconWebSocketClientImpl : FloconWebSocketClient {

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .trustFloconDesktopCertificate()
        .build()

    private val webSocket = AtomicReference<WebSocket?>(null)

    @Throws(Throwable::class)
    override suspend fun connect(
        address: String,
        port: Int,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit,
    ) {
        // if already connected, do nothing
        if (webSocket.get() != null) return

        val notifyClosed = onClosed

        return suspendCancellableCoroutine { continuation ->
            val connectionListener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    FloconLogger.log("✅ WebSocket Connection Opened!")

                    this@FloconWebSocketClientImpl.webSocket.set(webSocket)
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    FloconLogger.log("WEBSOCKET <----- Receiving $text")

                    onMessageReceived(text)
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    FloconLogger.log("🔒 WebSocket closing: $code $reason")
                    webSocket.close(code, null)
                    connectionEnded(webSocket, notifyClosed)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    connectionEnded(webSocket, notifyClosed)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    FloconLogger.logError("❌ WebSocket Connection Failed: ${t.message}", t)
                    if (continuation.isActive) {
                        // never opened: resume the coroutine with the exception
                        continuation.resumeWithException(t)
                    } else {
                        // an open connection dropped
                        connectionEnded(webSocket, notifyClosed)
                    }
                }
            }

            val request = Request.Builder()
                .url("wss://$address:$port")
                .header("User-Agent", "Flocon")
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

    /**
     * Called when the server closes the connection or the connection drops.
     *
     * The identity check avoids notifying twice, or after an explicit [disconnect].
     */
    private fun connectionEnded(closedSocket: WebSocket, onClosed: () -> Unit) {
        if (!webSocket.compareAndSet(closedSocket, null)) return
        onClosed()
    }

    override suspend fun sendMessage(message: String): Boolean {
        // not connected: the caller keeps the message
        val socket = webSocket.get() ?: return false
        FloconLogger.log("WEBSOCKET ----> send $message")
        return socket.send(message)
    }

    override suspend fun disconnect() {
        webSocket.getAndSet(null)?.close(1000, "Client disconnecting")
    }
}