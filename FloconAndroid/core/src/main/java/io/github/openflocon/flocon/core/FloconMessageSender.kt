package io.github.openflocon.flocon.core

interface FloconMessageSender {
    fun send(
        plugin: String,
        method: String,
        body: String,
    )
}