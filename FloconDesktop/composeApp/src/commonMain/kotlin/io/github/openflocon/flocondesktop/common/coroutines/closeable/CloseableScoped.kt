package io.github.openflocon.domain.common.coroutines.closeable

import kotlinx.coroutines.CoroutineScope

interface CloseableScoped : AutoCloseable {
    val coroutineScope: CoroutineScope
}
