package io.github.openflocon.flocon.database.core

/**
 * A reentrant lock, to follow the transactions of a database whatever the thread that logs its queries.
 */
internal expect class FloconLock() {
    fun <T> withLock(block: () -> T): T
}
