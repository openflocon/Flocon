package com.flocon.data.remote.bluetooth

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/** The byte stream to the phone, a serial port bound to the RFCOMM link. */
internal interface BridgeLink {
    val input: InputStream
    val output: OutputStream
    fun close()
}

/**
 * Forwards what the phone sends on its [BridgeLink] to the Flocon server of this desktop, and the
 * answers of the server back to the phone, so the server doesn't know the app is on Bluetooth:
 * - text frames go through a websocket on [websocketUrl], the server sees one session for the link
 * - files are re-assembled and posted to [uploadUrl], as the http client of the SDK would
 *
 * [httpClient] must have the websockets plugin installed.
 */
internal class BluetoothBridge(
    private val httpClient: HttpClient,
    private val websocketUrl: String,
    private val uploadUrl: String,
    private val onError: (message: String, throwable: Throwable?) -> Unit = { _, _ -> },
) {
    private class Transfer(val metadata: BluetoothFileStartMetadata, val file: File, val output: OutputStream)

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Bridges until the phone closes the link (returns), the link breaks or the frames are not valid
     * (throws). Cancelling closes the link.
     */
    suspend fun run(link: BridgeLink) = coroutineScope {
        val writeMutex = Mutex()
        val sessionMutex = Mutex()
        val transfers = mutableMapOf<Int, Transfer>()
        var session: DefaultClientWebSocketSession? = null

        suspend fun sendToPhone(frame: BluetoothFrame) = writeMutex.withLock {
            withContext(Dispatchers.IO) {
                link.output.write(frame.encode())
                link.output.flush()
            }
        }

        // the websocket to the server is opened with the first message, and again if the server restarted
        suspend fun serverSession(): DefaultClientWebSocketSession = sessionMutex.withLock {
            session?.takeIf { it.isActive }?.let { return@withLock it }
            httpClient.webSocketSession(websocketUrl).also { opened ->
                session = opened
                launch {
                    try {
                        for (frame in opened.incoming) {
                            if (frame is Frame.Text) sendToPhone(BluetoothFrame.Text(frame.readText()))
                        }
                    } catch (_: IOException) {
                        // the link to the phone broke, the read loop reports it
                    }
                }
            }
        }

        suspend fun forwardToServer(text: String) {
            try {
                serverSession().send(Frame.Text(text))
            } catch (first: Exception) {
                if (first is CancellationException) throw first
                try {
                    session = null // closed in the meantime, one more try on a new session
                    serverSession().send(Frame.Text(text))
                } catch (second: Exception) {
                    if (second is CancellationException) throw second
                    onError("Bluetooth: cannot reach the Flocon server, message dropped", second)
                }
            }
        }

        fun upload(transfer: Transfer) = launch {
            try {
                val response = httpClient.submitFormWithBinaryData(
                    url = uploadUrl,
                    formData = formData {
                        append("remotePath", transfer.metadata.remotePath)
                        append("requestId", transfer.metadata.requestId)
                        append("deviceId", transfer.metadata.deviceId)
                        append("appPackageName", transfer.metadata.appPackageName)
                        append("appInstance", transfer.metadata.appInstance.toString())
                        append(
                            "file",
                            transfer.file.readBytes(),
                            Headers.build {
                                append(HttpHeaders.ContentType, "application/octet-stream")
                                append(HttpHeaders.ContentDisposition, "filename=\"${transfer.metadata.fileName.safeFileName()}\"")
                            },
                        )
                    },
                )
                if (!response.status.isSuccess()) {
                    onError("Bluetooth: the file upload failed (${response.status})", null)
                }
            } catch (t: Exception) {
                if (t is CancellationException) throw t
                onError("Bluetooth: the file upload failed", t)
            } finally {
                transfer.file.delete()
            }
        }

        suspend fun handle(frame: BluetoothFrame) {
            when (frame) {
                is BluetoothFrame.Text -> forwardToServer(frame.text)

                is BluetoothFrame.FileStart -> {
                    val metadata = json.decodeFromString<BluetoothFileStartMetadata>(frame.metadata)
                    val file = File.createTempFile("flocon-bluetooth-${frame.transferId}-", ".bin")
                    transfers[frame.transferId] = Transfer(metadata, file, file.outputStream().buffered())
                }

                is BluetoothFrame.FileChunk -> transfers[frame.transferId]?.output?.write(frame.bytes)

                is BluetoothFrame.FileEnd -> transfers.remove(frame.transferId)?.let {
                    it.output.close()
                    upload(it)
                }
            }
        }

        // reads can't be interrupted: closing the link is what ends them when we are cancelled
        val closer = launch {
            try {
                awaitCancellation()
            } finally {
                link.close()
            }
        }
        try {
            withContext(Dispatchers.IO) {
                val decoder = BluetoothFrameDecoder()
                val buffer = ByteArray(READ_BUFFER_SIZE)
                try {
                    while (true) {
                        val read = link.input.read(buffer)
                        if (read < 0) break
                        decoder.feed(buffer, read).forEach { handle(it) }
                    }
                } catch (e: IOException) {
                    if (isActive) throw e // otherwise we closed the link ourselves
                }
            }
        } finally {
            closer.cancel()
            transfers.values.forEach {
                runCatching { it.output.close() }
                it.file.delete()
            }
            session?.cancel()
            link.close()
        }
    }

    private fun String.safeFileName() = substringAfterLast('/').substringAfterLast('\\').replace('"', '_')

    private companion object {
        const val READ_BUFFER_SIZE = 8 * 1024
    }
}
