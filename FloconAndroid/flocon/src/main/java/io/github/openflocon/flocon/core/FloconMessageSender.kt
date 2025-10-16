package io.github.openflocon.flocon.core

internal interface FloconMessageSender {
    fun send(
        plugin: String,
        method: String,
        body: String,
    )
}