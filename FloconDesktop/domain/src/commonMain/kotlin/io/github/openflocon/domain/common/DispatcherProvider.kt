package io.github.openflocon.domain.common

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val ui: CoroutineDispatcher
    val viewModel: CoroutineDispatcher
    val domain: CoroutineDispatcher
    val data: CoroutineDispatcher
}
