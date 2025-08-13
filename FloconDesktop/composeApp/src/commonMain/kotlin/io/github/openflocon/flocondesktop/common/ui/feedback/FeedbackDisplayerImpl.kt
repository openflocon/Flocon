package io.github.openflocon.flocondesktop.common.ui.feedback

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayerHandler.MessageToDisplayUi
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayerHandler.NotificationToDisplayUi
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

    private val _messagesToDisplay: Channel<MessageToDisplayUi> = Channel()
    override val messagesToDisplay: Flow<MessageToDisplayUi> = _messagesToDisplay.receiveAsFlow()

    private val _notificationsToDisplay: Channel<NotificationToDisplayUi> = Channel()
    override val notificationsToDisplay: Flow<NotificationToDisplayUi> = _notificationsToDisplay.receiveAsFlow()

    override fun displayMessage(
        message: String,
        type: FeedbackDisplayer.MessageType,
    ) {
        scope.launch {
            _messagesToDisplay.send(
                MessageToDisplayUi(
                    message = message,
                    type = type,
                    id = System.currentTimeMillis().toString(),
                ),
            )
        }
    }

    override fun displayNotification(title: String, message: String) {
        scope.launch {
            _notificationsToDisplay.send(
                NotificationToDisplayUi(
                    title = title,
                    message = message
                )
            )
        }
    }

}
