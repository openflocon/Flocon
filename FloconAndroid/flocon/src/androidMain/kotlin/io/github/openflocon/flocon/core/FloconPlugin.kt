package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.model.FloconMessageFromServer

internal interface FloconPlugin {
    fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    )
    fun onConnectedToServer()
}