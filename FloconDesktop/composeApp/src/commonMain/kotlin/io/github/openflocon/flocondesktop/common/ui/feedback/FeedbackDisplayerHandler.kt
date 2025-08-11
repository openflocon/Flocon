package io.github.openflocon.domain.common.ui.feedback

import kotlinx.coroutines.flow.Flow

interface FeedbackDisplayerHandler {
    val messagesToDisplay: Flow<MessageToDisplayUi>

    data class MessageToDisplayUi(
        val message: String,
        val type: FeedbackDisplayer.MessageType,
        val id: String,
    )
}
