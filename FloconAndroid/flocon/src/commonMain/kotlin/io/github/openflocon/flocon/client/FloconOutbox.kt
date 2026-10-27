package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconLogger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Keeps the encoded messages in order until they can be sent to the desktop app.
 *
 * Every message goes through the buffer, connected or not, and a single drain empties it under
 * the [mutex]: a message sent while the buffer is being flushed waits for the flush to end, then is
 * sent behind the others. Nothing is lost and nothing is sent out of order.
 *
 * @param capacity number of messages kept while not connected, at least 1. Above it, the oldest are dropped.
 * @param transport sends one message, returns `false` if it could not be sent.
 */
internal class FloconOutbox(
    capacity: Int,
    private val transport: suspend (String) -> Boolean,
) {
    private val capacity = maxOf(1, capacity)
    private val mutex = Mutex()
    private val queue = ArrayDeque<String>()
    private var isConnected = false
    private var dropped = 0

    /** Number of messages waiting to be sent. */
    suspend fun bufferedCount(): Int = mutex.withLock { queue.size }

    /** Number of messages dropped because the buffer was full. */
    suspend fun droppedCount(): Int = mutex.withLock { dropped }

    /** Sends the message, or keeps it if not connected. */
    suspend fun enqueue(message: String) = mutex.withLock {
        queue.addLast(message)
        if (queue.size > capacity) {
            queue.removeFirst()
            dropped++
            FloconLogger.log("Buffer full, the oldest message was dropped")
        }
        drain()
    }

    /** Starts sending: the [handshake] first, then the messages kept so far, then the new ones. */
    suspend fun connected(handshake: String) = mutex.withLock {
        queue.addFirst(handshake)
        isConnected = true
        drain()
    }

    /** Stops sending: the next messages are kept until [connected]. */
    suspend fun disconnected() = mutex.withLock {
        isConnected = false
    }

    private suspend fun drain() {
        while (isConnected) {
            val message = queue.firstOrNull() ?: return
            // removed once sent, so that a failure or a cancellation does not lose it
            if (transport(message)) {
                queue.removeFirst()
            } else {
                isConnected = false
            }
        }
    }
}
