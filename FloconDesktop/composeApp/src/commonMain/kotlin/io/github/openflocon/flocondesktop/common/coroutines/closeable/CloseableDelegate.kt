package io.github.openflocon.flocondesktop.common.coroutines.closeable

import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class CloseableDelegate(
    dispatcherProvider: DispatcherProvider,
) : CloseableScoped {
    private val dispatcher: CoroutineDispatcher = dispatcherProvider.viewModel

    override val coroutineScope = CoroutineScope(dispatcher + SupervisorJob())

    override fun close() {
        coroutineScope.cancel()
    }
}
