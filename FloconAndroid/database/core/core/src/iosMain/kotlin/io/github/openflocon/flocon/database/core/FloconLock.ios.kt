package io.github.openflocon.flocon.database.core

import platform.Foundation.NSRecursiveLock

internal actual class FloconLock actual constructor() {
    private val lock = NSRecursiveLock()

    actual fun <T> withLock(block: () -> T): T {
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }
}
