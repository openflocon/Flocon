package com.florent37.flocondesktop.common.coroutines.closeable

import kotlinx.coroutines.CoroutineScope

interface CloseableScoped : AutoCloseable {
    val coroutineScope: CoroutineScope
}
