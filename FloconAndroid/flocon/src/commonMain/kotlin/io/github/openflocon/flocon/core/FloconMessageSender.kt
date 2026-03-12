package io.github.openflocon.flocon.core

interface FloconMessageSender {

   suspend fun send(
        plugin: String,
        method: String,
        body: String,
    )

    suspend fun sendPendingMessages()

}