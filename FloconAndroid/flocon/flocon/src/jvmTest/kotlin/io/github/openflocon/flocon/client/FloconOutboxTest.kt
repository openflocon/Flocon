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

    private fun outbox(capacity: Int = 5_000) = FloconOutbox(capacity)
    private val link = FloconOutbox.Link { transport.send(it) }
    private suspend fun FloconOutbox.connected(handshake: String) = connected(link, handshake)
    private suspend fun FloconOutbox.disconnected() = disconnected(link)

    @Test
    fun `keeps the messages until connected`() = runBlocking {
        val outbox = outbox().also { it.add(link) }
        outbox.enqueue("a")
        outbox.enqueue("b")

        assertEquals(emptyList(), transport.sent.toList())
        assertEquals(2, outbox.bufferedCount())
    }

    @Test
    fun `flushes the handshake then the messages in order`() = runBlocking {
        val outbox = outbox().also { it.add(link) }
        val messages = (0 until 200).map { "m$it" }
        messages.forEach { outbox.enqueue(it) }

        outbox.connected(handshake = "hello")

        assertEquals(listOf("hello") + messages, transport.sent.toList())
        assertEquals(0, outbox.bufferedCount())
    }

    @Test
    fun `sends right away once connected`() = runBlocking {
        val outbox = outbox().also { it.add(link) }
        outbox.connected(handshake = "hello")
        outbox.enqueue("a")
        outbox.enqueue("b")

        assertEquals(listOf("hello", "a", "b"), transport.sent.toList())
    }

    @Test
    fun `keeps the messages emitted while flushing`() = runBlocking {
        val outbox = outbox().also { it.add(link) }
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
        val outbox = outbox(capacity = 3).also { it.add(link) }
        (1..5).forEach { outbox.enqueue("m$it") }

        assertEquals(3, outbox.bufferedCount())
        assertEquals(2, outbox.droppedCount())

        outbox.connected(handshake = "hello")
        assertEquals(listOf("hello", "m3", "m4", "m5"), transport.sent.toList())
    }

    @Test
    fun `keeps at least one message`() = runBlocking {
        val outbox = outbox(capacity = 0).also { it.add(link) }
        outbox.enqueue("a")
        outbox.enqueue("b")

        assertEquals(1, outbox.bufferedCount())
        outbox.connected(handshake = "hello")
        assertEquals(listOf("hello", "b"), transport.sent.toList())
    }

    @Test
    fun `keeps the messages of a lost connection for the next one`() = runBlocking {
        val outbox = outbox().also { it.add(link) }
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
        val outbox = outbox().also { it.add(link) }
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

    private class Desktop {
        val sent = CopyOnWriteArrayList<String>()
        val link = FloconOutbox.Link { sent += it; true }
    }

    @Test
    fun `sends to every connected address`() = runBlocking {
        val outbox = outbox()
        val localhost = Desktop()
        val remote = Desktop()
        outbox.add(localhost.link)
        outbox.add(remote.link)
        outbox.enqueue("a")

        outbox.connected(remote.link, handshake = "hello")
        outbox.enqueue("b")
        outbox.connected(localhost.link, handshake = "hi")
        outbox.enqueue("c")

        assertEquals(listOf("hello", "a", "b", "c"), remote.sent.toList())
        assertEquals(listOf("hi", "a", "b", "c"), localhost.sent.toList())
    }

    @Test
    fun `keeps a message while an address has not sent it`() = runBlocking {
        val outbox = outbox()
        val localhost = Desktop()
        val remote = Desktop()
        outbox.add(localhost.link)
        outbox.add(remote.link)
        outbox.connected(remote.link, handshake = "hello")
        outbox.enqueue("a")

        // localhost never connected: it still has to get "a" if it connects
        assertEquals(1, outbox.bufferedCount())

        outbox.remove(localhost.link)
        assertEquals(0, outbox.bufferedCount())
    }

    @Test
    fun `an address added late gets what is still kept`() = runBlocking {
        val outbox = outbox()
        val localhost = Desktop()
        outbox.add(localhost.link)
        outbox.enqueue("a")
        outbox.enqueue("b")

        val remote = Desktop()
        outbox.add(remote.link)
        outbox.connected(remote.link, handshake = "hello")

        assertEquals(listOf("hello", "a", "b"), remote.sent.toList())
    }
}
