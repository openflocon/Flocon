package com.florent37.flocondesktop.common.coroutines.dispatcherprovider

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val ui: CoroutineDispatcher
    val viewModel: CoroutineDispatcher
    val domain: CoroutineDispatcher
    val data: CoroutineDispatcher
}
