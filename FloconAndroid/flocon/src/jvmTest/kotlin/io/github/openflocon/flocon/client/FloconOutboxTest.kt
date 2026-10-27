package io.github.openflocon.flocon.client

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconOutboxTest {

    /** Records what the outbox sends, and can hold or fail one message. */
    private class FakeTransport {
        val sent = CopyOnWriteArrayList<String>()
        var holdOn: String? = null
        val held = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        var failOnceOn: String? = null

        suspend fun send(message: String): Boolean {
            if (message == holdOn) {
                holdOn = null
                held.complete(Unit)
                release.await()
            }
            if (message == failOnceOn) {
                failOnceOn = null
                return false
            }
            sent += message
            return true
        }
    }

    private val transport = FakeTransport()

    private fun outbox(capacity: Int = 5_000) = FloconOutbox(capacity) { transport.send(it) }

    @Test
    fun `keeps the messages until connected`() = runBlocking {
        val outbox = outbox()
        outbox.enqueue("a")
        outbox.enqueue("b")

        assertEquals(emptyList(), transport.sent.toList())
        assertEquals(2, outbox.bufferedCount())
    }

    @Test
    fun `flushes the handshake then the messages in order`() = runBlocking {
        val outbox = outbox()
        val messages = (0 until 200).map { "m$it" }
        messages.forEach { outbox.enqueue(it) }

        outbox.connected(handshake = "hello")

        assertEquals(listOf("hello") + messages, transport.sent.toList())
        assertEquals(0, outbox.bufferedCount())
    }

    @Test
    fun `sends right away once connected`() = runBlocking {
        val outbox = outbox()
        outbox.connected(handshake = "hello")
        outbox.enqueue("a")
        outbox.enqueue("b")

        assertEquals(listOf("hello", "a", "b"), transport.sent.toList())
    }

    @Test
    fun `keeps the messages emitted while flushing`() = runBlocking {
        val outbox = outbox()
        listOf("m0", "m1", "m2").forEach { outbox.enqueue(it) }
        transport.holdOn = "m1"

        val flush = launch { outbox.connected(handshake = "hello") }
        withTimeout(5_000) { transport.held.await() }
        // the flush is in the middle of "m1"
        val late1 = launch { outbox.enqueue("late1") }
        val late2 = launch { outbox.enqueue("late2") }
        transport.release.complete(Unit)
        flush.join()
        late1.join()
        late2.join()

        assertEquals(listOf("hello", "m0", "m1", "m2", "late1", "late2"), transport.sent.toList())
        assertEquals(0, outbox.bufferedCount())
    }

    @Test
    fun `drops the oldest messages above the capacity`() = runBlocking {
        val outbox = outbox(capacity = 3)
        (1..5).forEach { outbox.enqueue("m$it") }

        assertEquals(3, outbox.bufferedCount())
        assertEquals(2, outbox.droppedCount())

        outbox.connected(handshake = "hello")
        assertEquals(listOf("hello", "m3", "m4", "m5"), transport.sent.toList())
    }

    @Test
    fun `keeps at least one message`() = runBlocking {
        val outbox = outbox(capacity = 0)
        outbox.enqueue("a")
        outbox.enqueue("b")

        assertEquals(1, outbox.bufferedCount())
        outbox.connected(handshake = "hello")
        assertEquals(listOf("hello", "b"), transport.sent.toList())
    }

    @Test
    fun `keeps the messages of a lost connection for the next one`() = runBlocking {
        val outbox = outbox()
        outbox.connected(handshake = "hello")
        outbox.enqueue("a")
        outbox.disconnected()
        outbox.enqueue("b")
        outbox.enqueue("c")

        assertEquals(listOf("hello", "a"), transport.sent.toList())

        outbox.connected(handshake = "hello again")
        assertEquals(listOf("hello", "a", "hello again", "b", "c"), transport.sent.toList())
    }

    @Test
    fun `puts back a message that could not be sent`() = runBlocking {
        val outbox = outbox()
        listOf("a", "b", "c").forEach { outbox.enqueue(it) }
        transport.failOnceOn = "b"

        outbox.connected(handshake = "hello")
        // "b" failed: it and what follows wait for the next connection
        assertEquals(listOf("hello", "a"), transport.sent.toList())
        assertEquals(2, outbox.bufferedCount())

        outbox.enqueue("d")
        assertEquals(listOf("hello", "a"), transport.sent.toList())

        outbox.connected(handshake = "hello again")
        assertEquals(listOf("hello", "a", "hello again", "b", "c", "d"), transport.sent.toList())
    }
}
