package io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider

import io.github.openflocon.domain.common.DispatcherProvider
import kotlinx.coroutines.Dispatchers

class DispatcherProviderImpl : DispatcherProvider {
    override val ui = Dispatchers.Main
    override val viewModel = Dispatchers.Default
    override val domain = Dispatchers.Default
    override val data = Dispatchers.IO
}
