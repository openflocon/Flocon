package com.flocon.data.remote.bluetooth

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import io.ktor.server.websocket.WebSockets as ServerWebSockets

/** The phone is one end of a loopback socket, the bridge reads the other one. */
class BluetoothBridgeTest {

    private class SocketLink(private val socket: Socket) : BridgeLink {
        override val input = socket.getInputStream()
        override val output: OutputStream = socket.getOutputStream()
        override fun close() = socket.close()
    }

    private class Upload(val fields: Map<String, String>, val fileName: String?, val bytes: ByteArray)

    private val uploads = CompletableDeferred<Upload>()
    private val servers = mutableListOf<EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>>()
    private val clients = mutableListOf<HttpClient>()

    @AfterTest
    fun tearDown() {
        servers.forEach { it.stop(0, 0) }
        clients.forEach { it.close() }
    }

    private fun startServer(): Int {
        val server = embeddedServer(Netty, port = 0) {
            install(ServerWebSockets)
            routing {
                webSocket("/") {
                    for (frame in incoming) {
                        if (frame is Frame.Text) send(Frame.Text("echo:" + frame.readText()))
                    }
                }
                post("/upload") {
                    val fields = mutableMapOf<String, String>()
                    var fileName: String? = null
                    var bytes = ByteArray(0)
                    call.receiveMultipart().forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> part.name?.let { fields[it] = part.value }
                            is PartData.FileItem -> {
                                fileName = part.originalFileName
                                bytes = part.streamProvider().use { it.readBytes() }
                            }
                            else -> Unit
                        }
                        part.dispose()
                    }
                    uploads.complete(Upload(fields, fileName, bytes))
                    call.respondText("ok")
                }
            }
        }.start(wait = false)
        servers += server
        return runBlocking { server.engine.resolvedConnectors().first().port }
    }

    private fun bridge(serverPort: Int, onError: (String, Throwable?) -> Unit = { _, _ -> }): BluetoothBridge {
        val client = HttpClient(CIO) { install(WebSockets) }
        clients += client
        return BluetoothBridge(
            httpClient = client,
            websocketUrl = "ws://127.0.0.1:$serverPort/",
            uploadUrl = "http://127.0.0.1:$serverPort/upload",
            onError = onError,
        )
    }

    private fun linkPair(): Pair<Socket, SocketLink> {
        ServerSocket(0).use { listener ->
            val phone = Socket("127.0.0.1", listener.localPort).apply { soTimeout = 10_000 }
            return phone to SocketLink(listener.accept())
        }
    }

    private fun Socket.write(vararg frames: BluetoothFrame) {
        frames.forEach { getOutputStream().write(it.encode()) }
        getOutputStream().flush()
    }

    private fun Socket.readFrames(count: Int): List<BluetoothFrame> {
        val decoder = BluetoothFrameDecoder()
        val frames = mutableListOf<BluetoothFrame>()
        val buffer = ByteArray(8192)
        while (frames.size < count) {
            val read = getInputStream().read(buffer)
            check(read >= 0) { "link closed after ${frames.size} frames" }
            frames += decoder.feed(buffer, read)
        }
        return frames
    }

    private fun runBridge(bridge: BluetoothBridge, link: BridgeLink, phoneBlock: suspend (Unit) -> Unit) = runBlocking {
        withTimeout(30.seconds) {
            val running = async(Dispatchers.Default) { bridge.run(link) }
            withContext(Dispatchers.IO) { phoneBlock(Unit) }
            running.await()
        }
    }

    @Test
    fun `text goes to the server and the answer comes back to the phone`() {
        val (phone, link) = linkPair()

        runBridge(bridge(startServer()), link) {
            phone.write(BluetoothFrame.Text("hello"), BluetoothFrame.Text("héllo 🌨️"))

            assertEquals(
                setOf<BluetoothFrame>(BluetoothFrame.Text("echo:hello"), BluetoothFrame.Text("echo:héllo 🌨️")),
                phone.readFrames(2).toSet(),
            )
            phone.close()
        }
    }

    @Test
    fun `a file sent in chunks is uploaded to the server`() {
        val (phone, link) = linkPair()
        val content = ByteArray(40_000) { (it % 251).toByte() }
        val metadata = """{"remotePath":"files/a.bin","requestId":"req-1","deviceId":"dev","appPackageName":"com.app","appInstance":42,"fileName":"../a.bin"}"""

        runBridge(bridge(startServer()), link) {
            phone.write(BluetoothFrame.FileStart(3, metadata))
            content.toList().chunked(16 * 1024).forEach { chunk ->
                phone.write(BluetoothFrame.FileChunk(3, chunk.toByteArray()))
            }
            phone.write(BluetoothFrame.FileEnd(3))

            val upload = uploads.await()
            assertEquals(
                mapOf(
                    "remotePath" to "files/a.bin",
                    "requestId" to "req-1",
                    "deviceId" to "dev",
                    "appPackageName" to "com.app",
                    "appInstance" to "42",
                ),
                upload.fields,
            )
            assertEquals("a.bin", upload.fileName) // never a path
            assertContentEquals(content, upload.bytes)
            phone.close()
        }
    }

    @Test
    fun `run returns when the phone closes the link`() {
        val (phone, link) = linkPair()

        runBridge(bridge(startServer()), link) { phone.close() }
    }

    @Test
    fun `an invalid stream fails the bridge`() {
        val (phone, link) = linkPair()

        assertFailsWith<BluetoothProtocolException> {
            runBridge(bridge(startServer()), link) { phone.write(BluetoothFrame.Text("ok")); phone.getOutputStream().write(byteArrayOf(99, 0, 0, 0, 0)) }
        }
    }

    @Test
    fun `an unreachable server drops the message but keeps the link`() {
        val (phone, link) = linkPair()
        val errors = mutableListOf<String>()
        val unusedPort = ServerSocket(0).use { it.localPort }

        runBridge(bridge(unusedPort) { message, _ -> errors += message }, link) {
            phone.write(BluetoothFrame.Text("hello"))
            phone.close()
        }

        assertTrue(errors.any { "cannot reach" in it }, "errors: $errors")
    }
}
