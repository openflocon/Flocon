package io.github.openflocon.flocondesktop.common.ui.feedback

import io.github.openflocon.domain.common.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FeedbackDisplayerImpl(
    dispatcherProvider: DispatcherProvider,
) : FeedbackDisplayer,
    FeedbackDisplayerHandler {
    private val scope = CoroutineScope(dispatcherProvider.ui + SupervisorJob())

    private val _messagesToDisplay: Channel<FeedbackDisplayerHandler.MessageToDisplayUi> =
        Channel()
    override val messagesToDisplay: Flow<FeedbackDisplayerHandler.MessageToDisplayUi> =
        _messagesToDisplay.receiveAsFlow()

    override fun displayMessage(
        message: String,
        type: FeedbackDisplayer.MessageType,
    ) {
        scope.launch {
            _messagesToDisplay.send(
                FeedbackDisplayerHandler.MessageToDisplayUi(
                    message = message,
                    type = type,
                    id = System.currentTimeMillis().toString(),
                ),
            )
        }
    }
}
