package com.github.openflocon.flocon.core

import com.github.openflocon.flocon.model.FloconMessageFromServer

interface FloconPlugin {
    fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    )
    fun onConnectedToServer(
        sender: FloconMessageSender,
    )
}

