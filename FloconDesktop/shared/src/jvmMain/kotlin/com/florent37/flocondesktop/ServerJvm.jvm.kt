package com.florent37.flocondesktop

import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

class ServerJvm : Server {
    private val _receivedMessages = MutableSharedFlow<FloconIncomingMessageDataModel>()
    override val receivedMessages = _receivedMessages.asSharedFlow()

    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    private val activeSessions = ConcurrentHashMap<DeviceId, WebSocketSession>()

    override fun start(port: Int) {
        if (server != null) return

        val server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
            embeddedServer(Netty, port = port) {
                install(WebSockets) {
                    pingPeriod = 15.seconds
                    timeout = 15.seconds
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
                routing {
                    webSocket("/") {
                        println("WebSocket connection established!")

                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val receivedText = frame.readText()
                                    println("<---- Received raw message: $receivedText")
                                    try {
                                        val floconIncomingMessageDataModel =
                                            Json.decodeFromString<FloconIncomingMessageDataModel>(
                                                receivedText,
                                            )
                                        activeSessions.put(floconIncomingMessageDataModel.id, this)
                                        println("+ new client : ${floconIncomingMessageDataModel.id}")
                                        _receivedMessages.emit(floconIncomingMessageDataModel)
                                        // Access other fields of floconMessage as needed
                                    } catch (e: Exception) {
                                        println("Error parsing JSON message: ${e.message}")
                                        // Optionally send an error back to the client
                                        // outgoing.send(Frame.Text("Error: Could not parse message as FloconIncomingMessageDataModel. ${e.message}"))
                                    }
                                }
                                is Frame.Binary -> {
                                    println("Received binary message (not printed): ${frame.data.size} bytes")
                                }
                                is Frame.Close -> {
                                    val reason = frame.readReason()
                                    println("WebSocket connection closed: ${reason?.message}")
                                }
                                is Frame.Ping -> {
                                    println("Received Ping frame.")
                                }
                                is Frame.Pong -> {
                                    println("Received Pong frame.")
                                }
                            }
                        }
                    }
                }
            }.also { this.server = it }
        println("server started on $port")
        server.start(wait = false)
    }

    /**
     * Sends a message to a specific WebSocket client.
     *
     * @param targetSession The WebSocketSession of the client to send the message to.
     * @param message The message to send.
     */
    override suspend fun sendMessageToClient(
        deviceId: DeviceId,
        message: FloconOutgoingMessageDataModel,
    ) {
        val session = activeSessions[deviceId]
        if (session != null) {
            try {
                val jsonMessage =
                    Json.encodeToString(
                        FloconOutgoingMessageDataModel.serializer(),
                        message,
                    ) // Assuming you have a serializer for FloconIncomingMessageDataModel
                session.outgoing.send(Frame.Text(jsonMessage))
                println("------> Message sent to client: $message")
            } catch (e: Exception) {
                println("Error sending message to client: ${e.message}")
                // The session might have closed unexpectedly
                activeSessions.remove(deviceId)
            }
        } else {
            println("Target session is no longer active. deviceId=$deviceId, message=$message")
        }
    }

    override fun stop() {
        server?.stop(1000, 2000)
        server = null
    }
}
