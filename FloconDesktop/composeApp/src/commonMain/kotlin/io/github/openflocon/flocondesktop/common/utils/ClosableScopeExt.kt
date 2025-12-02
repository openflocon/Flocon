package io.github.openflocon.flocondesktop.common.utils

import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

context(closableScope: CloseableScoped)
fun <T> Flow<T>.stateInWhileSubscribed(default: T): StateFlow<T> = stateIn(
    scope = closableScope.coroutineScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = default
)
