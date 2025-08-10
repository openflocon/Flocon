package io.github.openflocon.flocon.model

data class FloconMessageFromServer(
    val plugin: String,
    val method: String,
    val body: String,
)