package io.github.openflocon.domain.common.ui.feedback

interface FeedbackDisplayer {
    fun displayMessage(
        message: String,
        type: MessageType = MessageType.Success,
    )

    enum class MessageType {
        Success,
        Error,
    }
}
