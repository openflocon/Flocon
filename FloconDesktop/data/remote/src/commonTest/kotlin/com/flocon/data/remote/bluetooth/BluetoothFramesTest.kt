package com.flocon.data.remote.bluetooth

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BluetoothFramesTest {

    private val frames = listOf(
        BluetoothFrame.Text("""{"plugin":"network","body":"héllo 🌨️"}"""),
        BluetoothFrame.FileStart(transferId = 7, metadata = """{"remotePath":"a/b.txt"}"""),
        BluetoothFrame.FileChunk(transferId = 7, bytes = byteArrayOf(0, 1, -1, 127, -128)),
        BluetoothFrame.FileEnd(transferId = 7),
        BluetoothFrame.Text(""),
    )

    @Test
    fun `every frame survives an encode then decode`() {
        frames.forEach { frame ->
            assertEquals(listOf(frame), BluetoothFrameDecoder().feed(frame.encode()))
        }
    }

    @Test
    fun `layout is type, big endian length, payload`() {
        val bytes = BluetoothFrame.Text("ab").encode()

        assertContentEquals(byteArrayOf(1, 0, 0, 0, 2, 'a'.code.toByte(), 'b'.code.toByte()), bytes)
    }

    @Test
    fun `several frames in one read`() {
        val stream = frames.map { it.encode() }.reduce { a, b -> a + b }

        assertEquals(frames, BluetoothFrameDecoder().feed(stream))
    }

    @Test
    fun `frames split anywhere, even byte by byte`() {
        val stream = frames.map { it.encode() }.reduce { a, b -> a + b }
        val decoder = BluetoothFrameDecoder()

        val decoded = stream.flatMap { byte -> decoder.feed(byteArrayOf(byte)) }

        assertEquals(frames, decoded)
    }

    @Test
    fun `reads bigger than the buffer and a partial buffer length`() {
        val big = BluetoothFrame.FileChunk(1, ByteArray(100_000) { it.toByte() })
        val stream = big.encode() + BluetoothFrame.FileEnd(1).encode()
        val decoder = BluetoothFrameDecoder()
        val decoded = mutableListOf<BluetoothFrame>()
        // the read buffer is reused, only [length] bytes are valid
        val readBuffer = ByteArray(8192)
        stream.toList().chunked(8192).forEach { chunk ->
            chunk.forEachIndexed { i, b -> readBuffer[i] = b }
            decoded += decoder.feed(readBuffer, chunk.size)
        }

        assertEquals(listOf(big, BluetoothFrame.FileEnd(1)), decoded)
    }

    @Test
    fun `an unknown type is rejected`() {
        assertFailsWith<BluetoothProtocolException> {
            BluetoothFrameDecoder().feed(byteArrayOf(99, 0, 0, 0, 0))
        }
    }

    @Test
    fun `a frame bigger than the limit is rejected before reading its payload`() {
        assertFailsWith<BluetoothProtocolException> {
            BluetoothFrameDecoder(maxPayloadSize = 10).feed(byteArrayOf(1, 0, 0, 0, 11))
        }
        assertFailsWith<BluetoothProtocolException> {
            BluetoothFrameDecoder().feed(byteArrayOf(1, -1, -1, -1, -1))
        }
    }

    @Test
    fun `a file frame without a transfer id is rejected`() {
        assertFailsWith<BluetoothProtocolException> {
            BluetoothFrameDecoder().feed(byteArrayOf(4, 0, 0, 0, 2, 0, 0))
        }
    }
}
