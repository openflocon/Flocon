package io.github.openflocon.flocon.database.core

internal actual class FloconLock actual constructor() {
    actual fun <T> withLock(block: () -> T): T = synchronized(this) { block() }
}
