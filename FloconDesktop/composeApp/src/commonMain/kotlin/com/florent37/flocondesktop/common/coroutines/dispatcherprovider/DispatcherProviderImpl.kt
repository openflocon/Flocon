package com.florent37.flocondesktop.common.coroutines.dispatcherprovider

import kotlinx.coroutines.Dispatchers

class DispatcherProviderImpl : DispatcherProvider {
    override val ui = Dispatchers.Main
    override val viewModel = Dispatchers.Default
    override val domain = Dispatchers.Default
    override val data = Dispatchers.IO
}
