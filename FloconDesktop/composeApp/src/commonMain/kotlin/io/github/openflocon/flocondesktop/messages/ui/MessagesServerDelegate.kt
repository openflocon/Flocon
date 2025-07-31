package io.github.openflocon.flocondesktop.messages.ui

import com.florent37.flocondesktop.common.coroutines.closeable.CloseableDelegate
import com.florent37.flocondesktop.common.coroutines.closeable.CloseableScoped
import com.florent37.flocondesktop.messages.domain.HandleIncomingMessagesUseCase
import com.florent37.flocondesktop.messages.domain.StartServerUseCase
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
