package io.github.openflocon.flocondesktop.messages.ui

import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.messages.domain.HandleIncomingMessagesUseCase
import io.github.openflocon.flocondesktop.messages.domain.StartServerUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MessagesServerDelegate(
    private val startServerUseCase: StartServerUseCase,
    private val handleIncomingMessagesUseCase: HandleIncomingMessagesUseCase,
    private val closeableDelegate: CloseableDelegate,
) : CloseableScoped by closeableDelegate {
    fun initialize() {
        startServerUseCase()

        coroutineScope.launch {
            handleIncomingMessagesUseCase()
                .collect()
        }
    }
}
