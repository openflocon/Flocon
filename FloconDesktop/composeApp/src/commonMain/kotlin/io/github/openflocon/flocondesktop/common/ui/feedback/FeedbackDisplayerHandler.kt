package io.github.openflocon.flocondesktop.common.ui.feedback

import kotlinx.coroutines.flow.Flow

interface FeedbackDisplayerHandler {
    val messagesToDisplay: Flow<MessageToDisplayUi>
    val notificationsToDisplay: Flow<NotificationToDisplayUi>

    data class MessageToDisplayUi(
        val message: String,
        val type: FeedbackDisplayer.MessageType,
        val id: String,
    )

    data class NotificationToDisplayUi(
        val title: String,
        val message: String,
    )
}
