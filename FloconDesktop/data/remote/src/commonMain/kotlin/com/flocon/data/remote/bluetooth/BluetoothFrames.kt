package com.flocon.data.remote.bluetooth

/** A Bluetooth RFCOMM link is a byte stream, so every message is framed. See `protocol/transport-bluetooth.md`. */
internal sealed interface BluetoothFrame {
    /** One websocket text message, in both directions. */
    data class Text(val text: String) : BluetoothFrame

    /** The beginning of a file the app sends. [metadata] is a [BluetoothFileStartMetadata] as json. */
    data class FileStart(val transferId: Int, val metadata: String) : BluetoothFrame

    class FileChunk(val transferId: Int, val bytes: ByteArray) : BluetoothFrame {
        override fun equals(other: Any?) =
            other is FileChunk && transferId == other.transferId && bytes.contentEquals(other.bytes)

        override fun hashCode() = 31 * transferId + bytes.contentHashCode()
    }

    data class FileEnd(val transferId: Int) : BluetoothFrame
}

internal class BluetoothProtocolException(message: String) : Exception(message)

private const val TYPE_TEXT: Byte = 1
private const val TYPE_FILE_START: Byte = 2
private const val TYPE_FILE_CHUNK: Byte = 3
private const val TYPE_FILE_END: Byte = 4

private const val HEADER_SIZE = 5 // type (1 byte) + payload length (4 bytes, big endian)
internal const val MAX_FRAME_PAYLOAD_SIZE = 32 * 1024 * 1024

/** `[type: 1 byte][payload length: 4 bytes, big endian][payload]` */
internal fun BluetoothFrame.encode(): ByteArray {
    val (type, payload) = when (this) {
        is BluetoothFrame.Text -> TYPE_TEXT to text.encodeToByteArray()
        is BluetoothFrame.FileStart -> TYPE_FILE_START to (transferId.toBytes() + metadata.encodeToByteArray())
        is BluetoothFrame.FileChunk -> TYPE_FILE_CHUNK to (transferId.toBytes() + bytes)
        is BluetoothFrame.FileEnd -> TYPE_FILE_END to transferId.toBytes()
    }
    return byteArrayOf(type) + payload.size.toBytes() + payload
}

/**
 * Rebuilds the frames from the chunks read on the stream, a frame can be split between two reads
 * and one read can carry several frames.
 */
internal class BluetoothFrameDecoder(private val maxPayloadSize: Int = MAX_FRAME_PAYLOAD_SIZE) {

    private var pending = ByteArray(INITIAL_CAPACITY)
    private var pendingSize = 0

    /** @throws BluetoothProtocolException when the stream is not valid, the link must then be closed. */
    fun feed(bytes: ByteArray, length: Int = bytes.size): List<BluetoothFrame> {
        if (pendingSize + length > pending.size) {
            pending = pending.copyOf(maxOf(pending.size * 2, pendingSize + length))
        }
        bytes.copyInto(pending, destinationOffset = pendingSize, startIndex = 0, endIndex = length)
        pendingSize += length

        val frames = mutableListOf<BluetoothFrame>()
        var start = 0
        while (pendingSize - start >= HEADER_SIZE) {
            val payloadSize = pending.readInt(start + 1)
            if (payloadSize < 0 || payloadSize > maxPayloadSize) {
                throw BluetoothProtocolException("Invalid frame size $payloadSize")
            }
            if (pendingSize - start - HEADER_SIZE < payloadSize) break // wait for the rest
            val payload = pending.copyOfRange(start + HEADER_SIZE, start + HEADER_SIZE + payloadSize)
            frames += decodeFrame(pending[start], payload)
            start += HEADER_SIZE + payloadSize
        }
        if (start > 0) {
            pending.copyInto(pending, destinationOffset = 0, startIndex = start, endIndex = pendingSize)
            pendingSize -= start
        }
        return frames
    }

    private fun decodeFrame(type: Byte, payload: ByteArray): BluetoothFrame = when (type) {
        TYPE_TEXT -> BluetoothFrame.Text(payload.decodeToString())
        TYPE_FILE_START -> BluetoothFrame.FileStart(payload.requireTransferId(), payload.copyOfRange(4, payload.size).decodeToString())
        TYPE_FILE_CHUNK -> BluetoothFrame.FileChunk(payload.requireTransferId(), payload.copyOfRange(4, payload.size))
        TYPE_FILE_END -> BluetoothFrame.FileEnd(payload.requireTransferId())
        else -> throw BluetoothProtocolException("Unknown frame type $type")
    }

    private fun ByteArray.requireTransferId(): Int {
        if (size < 4) throw BluetoothProtocolException("Frame too short")
        return readInt(0)
    }

    private companion object {
        const val INITIAL_CAPACITY = 8 * 1024
    }
}

private fun Int.toBytes() = byteArrayOf((this ushr 24).toByte(), (this ushr 16).toByte(), (this ushr 8).toByte(), toByte())

private fun ByteArray.readInt(index: Int): Int =
    ((this[index].toInt() and 0xFF) shl 24) or
        ((this[index + 1].toInt() and 0xFF) shl 16) or
        ((this[index + 2].toInt() and 0xFF) shl 8) or
        (this[index + 3].toInt() and 0xFF)
