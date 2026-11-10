package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconLogger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Keeps the encoded messages in order until they can be sent to the desktop app(s).
 *
 * Every message goes through one log, connected or not. Each [Link] (one per desktop address) reads
 * the log from its own position, and a single drain under the [mutex] sends to the connected links:
 * a message sent while the log is being flushed waits for the flush to end, then is sent behind the
 * others. Nothing is lost and nothing is sent out of order.
 *
 * A message is removed from the log once every link has sent it.
 *
 * @param capacity number of messages kept, at least 1. Above it, the oldest are dropped.
 */
internal class FloconOutbox(capacity: Int) {

    /**
     * A connection to one desktop address.
     *
     * @param transport sends one message, returns `false` if it could not be sent.
     */
    class Link(internal val transport: suspend (String) -> Boolean) {
        internal var isConnected = false
        internal var pendingHandshake: String? = null

        // sequence number of the next message to send
        internal var cursor = 0L
    }

    private val capacity = maxOf(1, capacity)
    private val mutex = Mutex()
    private val log = ArrayDeque<String>()
    private val links = mutableListOf<Link>()

    // sequence number of log.first()
    private var firstSequence = 0L
    private var dropped = 0

    /** Number of messages kept, waiting to be sent by at least one link. */
    suspend fun bufferedCount(): Int = mutex.withLock { log.size }

    /** Number of messages dropped because the buffer was full. */
    suspend fun droppedCount(): Int = mutex.withLock { dropped }

    /** Registers a link: it will receive what is still kept, then the next messages. */
    suspend fun add(link: Link) = mutex.withLock {
        link.cursor = firstSequence
        links += link
    }

    /** Unregisters a link: what only it was keeping is released. */
    suspend fun remove(link: Link) = mutex.withLock {
        links -= link
        trim()
    }

    /** Sends the message to the connected links, and keeps it for the others. */
    suspend fun enqueue(message: String) = mutex.withLock {
        log.addLast(message)
        if (log.size > capacity) {
            log.removeFirst()
            firstSequence++
            dropped++
            FloconLogger.log("Buffer full, the oldest message was dropped")
        }
        drain()
    }

    /** Starts sending on [link]: the [handshake] first, then the messages kept so far, then the new ones. */
    suspend fun connected(link: Link, handshake: String) = mutex.withLock {
        link.pendingHandshake = handshake
        link.isConnected = true
        drain()
    }

    /** Stops sending on [link]: the next messages are kept until [connected]. */
    suspend fun disconnected(link: Link) = mutex.withLock {
        link.isConnected = false
    }

    private suspend fun drain() {
        for (link in links) {
            drain(link)
        }
        trim()
    }

    private suspend fun drain(link: Link) {
        if (link.cursor < firstSequence) {
            // the messages it did not send have been dropped
            link.cursor = firstSequence
        }
        while (link.isConnected) {
            val handshake = link.pendingHandshake
            if (handshake != null) {
                if (link.transport(handshake)) link.pendingHandshake = null else link.isConnected = false
                continue
            }
            val message = log.getOrNull((link.cursor - firstSequence).toInt()) ?: return
            // moves on once sent, so that a failure or a cancellation does not lose it
            if (link.transport(message)) link.cursor++ else link.isConnected = false
        }
    }

    /** Removes what every link has sent. Nothing is removed while there is no link. */
    private fun trim() {
        val sentByAll = links.minOfOrNull { it.cursor } ?: return
        while (firstSequence < sentByAll && log.isNotEmpty()) {
            log.removeFirst()
            firstSequence++
        }
    }
}
