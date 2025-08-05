package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.model.FloconMessageFromServer

interface FloconPlugin {
    fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    )
    fun onConnectedToServer(
        sender: FloconMessageSender,
    )
}

