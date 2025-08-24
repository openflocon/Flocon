package com.flocon.data.remote.server

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
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
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

class ServerJvm : Server {
    private val _receivedMessages = MutableSharedFlow<FloconIncomingMessageDataModel>()
    override val receivedMessages = _receivedMessages.asSharedFlow()

    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? =
        null
    private val isStarted = AtomicBoolean(false)
    private val _activeSessions =
        MutableStateFlow(emptyMap<FloconDeviceIdAndPackageNameDataModel, WebSocketSession>())

    override val activeDevices = _activeSessions.map { it.keys }
        .distinctUntilChanged()

    override fun start(port: Int) {
        if (server != null && isStarted.get()) return

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
                        val currentSession = this // Store the current session to use in finally block

                        // Use a try-catch block to handle the exception when the channel is closed.
                        try {
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
                                            val device = FloconDeviceIdAndPackageNameDataModel(
                                                deviceId = floconIncomingMessageDataModel.deviceId,
                                                packageName = floconIncomingMessageDataModel.appPackageName,
                                                appInstance = floconIncomingMessageDataModel.appInstance,
                                            )
                                            _activeSessions.update {
                                                it + (device to currentSession)
                                            }
                                            // println("+ new client : ${floconIncomingMessageDataModel.deviceId}")
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
                                        _activeSessions.update { map ->
                                            map.filterValues { it != currentSession }
                                        }
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
                        } catch (e: ClosedReceiveChannelException) {
                            println("WebSocket connection closed (channel closed): ${closeReason.await()}")
                        } catch (e: Exception) {
                            println("WebSocket error: ${e.message}")
                        } finally {
                            // This block will always be executed when the coroutine ends,
                            // whether the connection was closed cleanly or due to an error.
                            _activeSessions.update { map ->
                                map.filterValues { it != currentSession }
                            }
                            println("WebSocket : Session removed from active sessions.")
                        }
                    }
                }
            }.also { this.server = it }
        println("server started on $port")

        try {
            server.start(wait = false)
            isStarted.set(true)
        } catch (t: Throwable) {
            isStarted.set(false)
            throw t
        }
    }

    /**
     * Sends a message to a specific WebSocket client.
     *
     * @param targetSession The WebSocketSession of the client to send the message to.
     * @param message The message to send.
     */
    override suspend fun sendMessageToClient(
        deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel,
        message: FloconOutgoingMessageDataModel
    ) {
        val session = _activeSessions.value[deviceIdAndPackageName]
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
                _activeSessions.update { map ->
                    map.filterKeys { it != deviceIdAndPackageName }
                }
            }
        } else {
            println("Target session is no longer active. deviceId=$deviceIdAndPackageName, message=$message")
        }
    }

    override fun stop() {
        server?.stop(1000, 2000)
        server = null
    }
}
