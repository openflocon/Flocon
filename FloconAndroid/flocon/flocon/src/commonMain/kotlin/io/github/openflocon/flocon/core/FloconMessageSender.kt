package io.github.openflocon.flocon.core

interface FloconMessageSender {

    fun send(
        plugin: String,
        method: String,
        body: String,
    )

    /**
     * Not needed anymore: the messages sent while the app is not connected to the desktop are kept,
     * and sent on connection. Kept so that existing implementations still compile.
     */
    fun sendPendingMessages()

}