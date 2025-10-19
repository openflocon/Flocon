package io.github.openflocon.flocon.websocket

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class MessageQueue(private val capacity: Int) {

    private val queueMutex = Mutex()
    private val queue = ArrayDeque<String>()

    suspend fun add(message: String) = queueMutex.withLock {
        if (queue.size >= capacity) {
            // if the queue is full, remove the oldest message
            queue.removeFirst()
        }
        queue.addLast(message)
    }

    suspend fun poll(): String? = queueMutex.withLock {
        queue.removeFirstOrNull()
    }
}