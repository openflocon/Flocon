package io.github.openflocon.flocon.websocket


import io.github.openflocon.flocon.FloconLogger
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.*
import io.ktor.http.HttpMethod
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.*

internal actual fun buildFloconWebSocketClient(): FloconWebSocketClient {
    return FloconWebSocketClientJvm()
}

internal class FloconWebSocketClientJvm() : FloconWebSocketClient {

    private val client = HttpClient(CIO.create()) {
        install(WebSockets)
    }

    private var session: DefaultClientWebSocketSession? = null
    private val queue = MessageQueue(capacity = 200)

    override suspend fun connect(
        address: String,
        port: Int,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit,
    ) {
        // déjà connecté → on ne fait rien
        if (session != null) return

        try {
            FloconLogger.log("🔌 Tentative de connexion à ws://$address:$port ...")

            val wsSession = client.webSocketSession(
                method = HttpMethod.Get,
                host = address,
                port = port,
                path = "/"
            )

            session = wsSession
            FloconLogger.log("✅ WebSocket connecté à ws://$address:$port")

            // Lancer un job pour écouter les messages entrants
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    for (frame in wsSession.incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                FloconLogger.log("WEBSOCKET <----- Reçu : $text")
                                onMessageReceived(text)
                            }
                            is Frame.Close -> {
                                FloconLogger.log("🔒 WebSocket fermé (${frame.readReason()})")
                                disconnect()
                                onClosed()
                                break
                            }
                            else -> Unit
                        }
                    }
                    FloconLogger.log("❌ WebSocket fermé sans raison")
                } catch (e: Exception) {
                    FloconLogger.logError("❌ Erreur WebSocket : ${e.message}", e)
                } finally {
                    disconnect()
                    onClosed()
                }
            }

        } catch (t: Throwable) {
            FloconLogger.logError("❌ Échec de connexion WebSocket : ${t.message}", t)
            throw t
        }
    }

    override suspend fun sendMessage(message: String): Boolean {
        val currentSession = session
        if (currentSession == null) {
            queue.add(message)
            FloconLogger.log("🕒 WebSocket non connecté, message mis en file : $message")
            return false
        }

        return try {
            currentSession.send(Frame.Text(message))
            FloconLogger.log("WEBSOCKET ----> Envoyé : $message")
            true
        } catch (e: Exception) {
            FloconLogger.logError("❌ Échec d’envoi : ${e.message}", e)
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
