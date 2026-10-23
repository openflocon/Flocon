package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconLogger
import io.ktor.client.engine.cio.CIO
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal actual fun buildFloconWebSocketClient(): FloconWebSocketClient {
    return FloconWebSocketClientJvm()
}

internal class FloconWebSocketClientJvm() : FloconWebSocketClient {

    private val client = HttpClient(CIO.create()) {
        install(WebSockets)
        defaultRequest {
            header(HttpHeaders.UserAgent, "Flocon")
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    private val queue = MessageQueue(capacity = 200)

    override suspend fun connect(
        address: String,
        port: Int,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit,
    ) {
        if (session != null) return

        try {
            FloconLogger.log("🔌 Trying to connect ws://$address:$port ...")

            val wsSession = client.webSocketSession(
                method = HttpMethod.Get,
                host = address,
                port = port,
                path = "/"
            )

            session = wsSession
            FloconLogger.log("✅ WebSocket connected at ws://$address:$port")

            // Lancer un job pour écouter les messages entrants
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    for (frame in wsSession.incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                FloconLogger.log("WEBSOCKET <----- received : $text")
                                onMessageReceived(text)
                            }
                            is Frame.Close -> {
                                FloconLogger.log("🔒 WebSocket closed (${frame.readReason()})")
                                disconnect()
                                onClosed()
                                break
                            }
                            else -> Unit
                        }
                    }
                    FloconLogger.log("❌ WebSocket closed without reason")
                } catch (e: Exception) {
                    FloconLogger.logError("❌ WebSocket error : ${e.message}", e)
                } finally {
                    disconnect()
                    onClosed()
                }
            }

        } catch (t: Throwable) {
            FloconLogger.logError("❌ WebSocket connection failed : ${t.message}", t)
            throw t
        }
    }

    override suspend fun sendMessage(message: String): Boolean {
        val currentSession = session
        if (currentSession == null) {
            queue.add(message)
            FloconLogger.log("🕒 WebSocket not connected, message saved in the queue : $message")
            return false
        }

        return try {
            currentSession.send(Frame.Text(message))
            FloconLogger.log("WEBSOCKET ----> Sent : $message")
            true
        } catch (e: Exception) {
            FloconLogger.logError("❌ Failed to send : ${e.message}", e)
            false
        }
    }

    override suspend fun sendPendingMessages() {
        var message = queue.poll()
        while (message != null) {
            sendMessage(message)
            message = queue.poll()
        }
    }

    override suspend fun disconnect() {
        session?.close(CloseReason(CloseReason.Codes.NORMAL, "Client disconnecting"))
        session = null
    }
}
