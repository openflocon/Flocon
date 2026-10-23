package io.github.openflocon.flocon.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.model.FloconFileInfo
import io.github.openflocon.flocon.websocket.MessageQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.OutputStream
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * The app is the RFCOMM server: it publishes the Flocon service and waits for the desktop to open
 * the serial port bound to it. [connect] returns once the desktop is connected.
 */
@SuppressLint("MissingPermission") // checked in connect()
internal class FloconBluetoothTransportAndroid(
    private val context: Context,
) : FloconBluetoothTransport {

    private class Connection(val socket: BluetoothSocket, val output: OutputStream)

    @Volatile
    private var connection: Connection? = null

    private val queue = MessageQueue(capacity = 200)
    private val writeMutex = Mutex()
    private val nextTransferId = AtomicInteger(0)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json

    override suspend fun connect(
        address: String,
        port: Int,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit,
    ) {
        if (connection != null) return

        val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
            ?: throw IllegalStateException("Flocon Bluetooth: this device has no Bluetooth adapter")
        if (!hasConnectPermission()) {
            throw SecurityException(
                "Flocon Bluetooth: the app must be granted ${Manifest.permission.BLUETOOTH_CONNECT} (request it at runtime)",
            )
        }
        if (!adapter.isEnabled) {
            throw IllegalStateException("Flocon Bluetooth: Bluetooth is turned off")
        }

        val serverSocket = adapter.listenUsingRfcommWithServiceRecord(
            FLOCON_BLUETOOTH_SERVICE_NAME,
            UUID.fromString(FLOCON_BLUETOOTH_SERVICE_UUID),
        )
        FloconLogger.log("🔌 Waiting for Flocon Desktop on Bluetooth (RFCOMM)...")
        val socket = try {
            suspendCancellableCoroutine<BluetoothSocket> { continuation ->
                // accept() blocks and can't be interrupted, closing the server socket unblocks it
                continuation.invokeOnCancellation { runCatching { serverSocket.close() } }
                Thread {
                    try {
                        continuation.resume(serverSocket.accept())
                    } catch (t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                }.apply { isDaemon = true }.start()
            }
        } finally {
            runCatching { serverSocket.close() } // one desktop at a time, stop advertising
        }

        val currentConnection = Connection(socket = socket, output = socket.outputStream)
        connection = currentConnection
        FloconLogger.log("✅ Bluetooth connected to ${socket.remoteDevice?.address}")

        scope.launch { readLoop(currentConnection, onMessageReceived, onClosed) }
    }

    private suspend fun readLoop(
        current: Connection,
        onMessageReceived: (String) -> Unit,
        onClosed: () -> Unit,
    ) {
        val decoder = BluetoothFrameDecoder()
        val buffer = ByteArray(READ_BUFFER_SIZE)
        try {
            val input = current.socket.inputStream
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                decoder.feed(buffer, read).forEach { frame ->
                    if (frame is BluetoothFrame.Text) {
                        FloconLogger.log("BLUETOOTH <----- received : ${frame.text}")
                        onMessageReceived(frame.text)
                    }
                }
            }
            FloconLogger.log("🔒 Bluetooth closed by the desktop")
        } catch (t: Throwable) {
            FloconLogger.logError("❌ Bluetooth error : ${t.message}", t)
        } finally {
            close(current)
            onClosed()
        }
    }

    override suspend fun sendMessage(message: String): Boolean {
        if (connection == null) {
            queue.add(message)
            FloconLogger.log("🕒 Bluetooth not connected, message saved in the queue : $message")
            return false
        }
        return write(BluetoothFrame.Text(message)).also {
            if (it) FloconLogger.log("BLUETOOTH ----> Sent : $message")
        }
    }

    override suspend fun sendPendingMessages() {
        var message = queue.poll()
        while (message != null) {
            sendMessage(message)
            message = queue.poll()
        }
    }

    override suspend fun send(
        file: FloconFile,
        infos: FloconFileInfo,
        address: String,
        port: Int,
        deviceId: String,
        appPackageName: String,
        appInstance: Long,
    ): Boolean {
        val transferId = nextTransferId.incrementAndGet()
        val metadata = BluetoothFileStartMetadata(
            remotePath = infos.path,
            requestId = infos.requestId,
            deviceId = deviceId,
            appPackageName = appPackageName,
            appInstance = appInstance,
            fileName = file.file.name,
        )
        // each frame is written on its own, the messages can be interleaved with the chunks
        if (!write(BluetoothFrame.FileStart(transferId, json.encodeToString(BluetoothFileStartMetadata.serializer(), metadata)))) return false
        val sent = try {
            withContext(Dispatchers.IO) {
                file.file.inputStream().use { input ->
                    val buffer = ByteArray(FILE_CHUNK_SIZE)
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        if (!write(BluetoothFrame.FileChunk(transferId, buffer.copyOf(read)))) return@withContext false
                    }
                    true
                }
            }
        } catch (t: Throwable) {
            FloconLogger.logError("❌ Failed to read the file to send : ${t.message}", t)
            false
        }
        return sent && write(BluetoothFrame.FileEnd(transferId))
    }

    private suspend fun write(frame: BluetoothFrame): Boolean {
        val current = connection ?: return false
        return try {
            withContext(Dispatchers.IO) {
                writeMutex.withLock {
                    current.output.write(frame.encode())
                    current.output.flush()
                }
            }
            true
        } catch (t: Throwable) {
            FloconLogger.logError("❌ Failed to send : ${t.message}", t)
            close(current) // the read loop notices and reports onClosed
            false
        }
    }

    override suspend fun disconnect() {
        connection?.let { close(it) }
    }

    private fun close(current: Connection) {
        if (connection === current) connection = null
        runCatching { current.socket.close() }
    }

    private fun hasConnectPermission() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
        context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED

    private companion object {
        const val READ_BUFFER_SIZE = 8 * 1024
        const val FILE_CHUNK_SIZE = 16 * 1024
    }
}
