package io.github.openflocon.flocon.database.core

// one thread
internal actual class FloconLock actual constructor() {
    actual fun <T> withLock(block: () -> T): T = block()
}
