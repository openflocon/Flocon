package com.flocon.data.remote.server

import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.WebSocket
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.CompletionStage
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerJvmTlsTest {

    private val json = Json { ignoreUnknownKeys = true }

    // same behavior as the SDKs: the self signed certificate is accepted without verification
    private val trustAllContext = SSLContext.getInstance("TLS").apply {
        init(
            null,
            arrayOf(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
                    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
                },
            ),
            SecureRandom(),
        )
    }

    private fun freePort() = ServerSocket(0).use { it.localPort }

    @Test
    fun `websocket is served over TLS and delivers messages`() {
        runBlocking {
            val port = freePort()
            val server = ServerJvm(json = json, networkRepository = lazy { error("not used") })
            server.startWebsocket(port)
            try {
                val client = HttpClient.newBuilder().sslContext(trustAllContext).build()
                val webSocket = client.newWebSocketBuilder()
                    .buildAsync(
                        URI("wss://localhost:$port"),
                        object : WebSocket.Listener {
                            override fun onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage<*>? {
                                webSocket.request(1)
                                return null
                            }
                        }
                    )
                    .get()
                val message = FloconIncomingMessageDataModel(
                    deviceName = "device",
                    deviceId = "id",
                    plugin = "plugin",
                    body = "{}",
                    method = "method",
                    appName = "app",
                    appPackageName = "com.app",
                    appInstance = 1L,
                    platform = "android",
                    versionName = "1.0",
                )
                webSocket.sendText(
                    json.encodeToString(FloconIncomingMessageDataModel.serializer(), message),
                    true,
                ).get()

                val received = withTimeout(5_000) { server.receivedMessages.first() }
                assertEquals(message, received)
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "done").get()
            } finally {
                server.stop()
            }
        }
    }

    @Test
    fun `cleartext connections are refused`() {
        val port = freePort()
        val server = ServerJvm(json = json, networkRepository = lazy { error("not used") })
        server.startWebsocket(port)
        try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder(URI("http://localhost:$port")).build()
            assertFailsWith<Exception> {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            }
        } finally {
            server.stop()
        }
    }
}
