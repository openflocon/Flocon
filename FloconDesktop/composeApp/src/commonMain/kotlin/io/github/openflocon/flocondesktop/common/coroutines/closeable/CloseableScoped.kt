package io.github.openflocon.flocondesktop.common.coroutines.closeable

import kotlinx.coroutines.CoroutineScope

interface CloseableScoped : AutoCloseable {
    val coroutineScope: CoroutineScope
}
