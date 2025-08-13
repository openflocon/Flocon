package io.github.openflocon.flocondesktop.common.ui.feedback

interface FeedbackDisplayer {

    fun displayMessage(
        message: String,
        type: MessageType = MessageType.Success,
    )

    fun displayNotification(
        title: String,
        message: String
    )

    enum class MessageType {
        Success,
        Error,
    }
}
